package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final AuctionRepository auctionRepository;
    private final AuctionFindService auctionFindService;
    private final AuctionRecordRepository auctionRecordRepository;
    private final MemberFindService memberFindService;
    private final ArtRepository artRepository;

    @Transactional
    public void bid(Long auctionId, Long bidderId, int bidAmount) {
        Auction auction = auctionFindService.findByIdWithPessimisticLock(auctionId);
        validateBidTime(auction);
        validateArtOwner(auction.getArt().getId(), bidderId);
        validateBidPrice(auction, bidAmount);

        Member newBidder = memberFindService.findById(bidderId);
        validateDuplicateBid(auction, newBidder);

        executeBidProcess(auction, newBidder, bidAmount);
    }

    private void validateBidTime(Auction auction) {
        if (!auction.isAuctionInProgress()) {
            throw AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_START_OR_ALREADY_FINISHED);
        }
    }

    private void validateArtOwner(Long artId, Long bidderId) {
        if (isOwnerBidRequest(artId, bidderId)) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_OWNER_BID);
        }
    }

    private boolean isOwnerBidRequest(Long artId, Long bidderId) {
        return artRepository.existsByIdAndOwnerId(artId, bidderId);
    }

    private void validateBidPrice(Auction auction, int bidAmount) {
        if (auction.isBidderExists()) {
            if (auction.getBidAmount() >= bidAmount) {
                throw AnotherArtException.type(AuctionErrorCode.INVALID_BID_PRICE);
            }
        } else {
            if (auction.getBidAmount() > bidAmount) {
                throw AnotherArtException.type(AuctionErrorCode.INVALID_BID_PRICE);
            }
        }
    }

    private void validateDuplicateBid(Auction auction, Member newBidder) {
        if (auction.isBidderExists() && auction.getBidder().isSameMember(newBidder.getId())) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_DUPLICATE_BID);
        }
    }

    private void executeBidProcess(Auction auction, Member newBidder, int bidAmount) {
        auction.applyNewBid(newBidder, bidAmount);
        auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, newBidder, bidAmount));
        auctionRepository.saveAndFlush(auction);
    }
}

package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidService {
    private final MemberFindService memberFindService;
    private final AuctionRepository auctionRepository;

    @Transactional
    public void bid(Long auctionId, Long bidderId, Integer bidPrice) {
        Auction auction = getAuction(auctionId);
        Member bidder = memberFindService.findById(bidderId);

        auction.applyNewBid(bidder, bidPrice);
    }

    private Auction getAuction(Long auctionId) {
        return auctionRepository.findByIdWithPessimisticLock(auctionId)
                .orElseThrow(() -> AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_FOUND));
    }
}

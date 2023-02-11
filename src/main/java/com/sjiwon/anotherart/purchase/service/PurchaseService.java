package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.service.ArtFindService;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.service.AuctionFindService;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final MemberFindService memberFindService;
    private final ArtFindService artFindService;
    private final AuctionFindService auctionFindService;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public void purchaseArt(Long artId, Long buyerId) {
        Art art = artFindService.findById(artId);
        validateArtOwnerPurchase(art, buyerId);
        try {
            if (art.isAuctionType()) {
                auctionArtPurchaseProcess(art, buyerId);
            } else {
                generalArtPurchaseProcess(art, buyerId);
            }
        } catch (DataIntegrityViolationException e) {
            throw AnotherArtException.type(PurchaseErrorCode.ART_ALREADY_SOLD_OUT);
        }
    }

    private void validateArtOwnerPurchase(Art art, Long buyerId) {
        if (art.isArtOwner(buyerId)) {
            throw AnotherArtException.type(PurchaseErrorCode.INVALID_OWNER_PURCHASE);
        }
    }

    private void auctionArtPurchaseProcess(Art art, Long buyerId) {
        Auction auction = auctionFindService.findByArtId(art.getId());
        validateAuctionInProgress(auction);
        validateHighestBidder(auction, buyerId);

        Member buyer = memberFindService.findById(buyerId);
        purchaseRepository.save(Purchase.purchaseArt(buyer, art, auction.getBidAmount()));
        proceedingPointTransaction(auction, art, buyer);
    }

    private void validateAuctionInProgress(Auction auction) {
        if (auction.isAuctionInProgress()) {
            throw AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED);
        }
    }

    private void validateHighestBidder(Auction auction, Long buyerId) {
        Member bidder = auction.getBidder();
        if (!bidder.isSameMember(buyerId)) {
            throw AnotherArtException.type(PurchaseErrorCode.INVALID_HIGHEST_BIDDER);
        }
    }

    private void generalArtPurchaseProcess(Art art, Long buyerId) {
        Member buyer = memberFindService.findById(buyerId);
        purchaseRepository.save(Purchase.purchaseArt(buyer, art, art.getPrice()));
        proceedingPointTransaction(null, art, buyer);
    }

    private void proceedingPointTransaction(@Nullable Auction auction, Art art, Member buyer) {
        Member owner = art.getOwner();
        if (auction == null) {
            owner.addPointDetail(PointDetail.insertPointDetail(owner, PointType.SOLD, art.getPrice()));
            buyer.addPointDetail(PointDetail.insertPointDetail(buyer, PointType.PURCHASE, art.getPrice()));
        } else {
            owner.addPointDetail(PointDetail.insertPointDetail(owner, PointType.SOLD, auction.getBidAmount()));
        }
    }
}

package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.service.ArtFindService;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.service.AuctionFindService;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.purchase.domain.Purchase;
import com.sjiwon.anotherart.purchase.domain.PurchaseRepository;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PurchaseService {
    private final ArtFindService artFindService;
    private final MemberFindService memberFindService;
    private final AuctionFindService auctionFindService;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public Long purchaseArt(Long artId, Long memberId) {
        Art art = artFindService.findByIdWithOwner(artId);
        Member buyer = memberFindService.findById(memberId);

        try {
            return processArtPurchase(art, buyer);
        } catch (DataIntegrityViolationException e) {
            throw AnotherArtException.type(PurchaseErrorCode.ALREADY_SOLD);
        }
    }

    private Long processArtPurchase(Art art, Member buyer) {
        if (art.isAuctionType()) {
            Auction auction = auctionFindService.findByArtId(art.getId());
            validateAuctionFinished(auction);
            validateHighestBidder(auction, buyer);

            Purchase purchaseAuctionArt = Purchase.purchaseAuctionArt(art, buyer, auction.getHighestBidPrice());
            return purchaseRepository.save(purchaseAuctionArt).getId();
        } else {
            Purchase purchasedGeneralArt = Purchase.purchaseGeneralArt(art, buyer);
            return purchaseRepository.save(purchasedGeneralArt).getId();
        }
    }

    private void validateAuctionFinished(Auction auction) {
        if (!auction.isFinished()) {
            throw AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED);
        }
    }

    private void validateHighestBidder(Auction auction, Member buyer) {
        if (!auction.isHighestBidder(buyer)) {
            throw AnotherArtException.type(PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER);
        }
    }
}
package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
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
    private final ArtRepository artRepository;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public Long purchaseArt(final Long artId, final Long memberId) {
        final Art art = artRepository.getByIdWithFetchOwner(artId);
        final Member buyer = memberRepository.getById(memberId);

        try {
            return processArtPurchase(art, buyer);
        } catch (final DataIntegrityViolationException e) {
            throw AnotherArtException.type(PurchaseErrorCode.ALREADY_SOLD);
        }
    }

    private Long processArtPurchase(final Art art, final Member buyer) {
        if (art.isAuctionType()) {
            final Auction auction = auctionRepository.getByArtId(art.getId());
            validateAuctionFinished(auction);
            validateHighestBidder(auction, buyer);

            final Purchase purchaseAuctionArt = Purchase.purchaseAuctionArt(art, buyer, auction.getHighestBidPrice());
            return purchaseRepository.save(purchaseAuctionArt).getId();
        } else {
            final Purchase purchasedGeneralArt = Purchase.purchaseGeneralArt(art, buyer);
            return purchaseRepository.save(purchasedGeneralArt).getId();
        }
    }

    private void validateAuctionFinished(final Auction auction) {
        if (!auction.isFinished()) {
            throw AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED);
        }
    }

    private void validateHighestBidder(final Auction auction, final Member buyer) {
        if (!auction.isHighestBidder(buyer)) {
            throw AnotherArtException.type(PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER);
        }
    }
}

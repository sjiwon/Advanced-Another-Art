package com.sjiwon.anotherart.purchase.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.purchase.exception.PurchaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.ALREADY_SOLD;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.ART_OWNER_CANNOT_PURCHASE_OWN;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.AUCTION_NOT_FINISHED;
import static com.sjiwon.anotherart.purchase.exception.PurchaseExceptionCode.BUYER_IS_NOT_HIGHEST_BIDDER;

@Service
@RequiredArgsConstructor
public class PurchaseInspector {
    public void checkAuctionArt(
            final Auction auction,
            final Art art,
            final Member buyer
    ) {
        if (auction.isInProgress()) {
            throw new PurchaseException(AUCTION_NOT_FINISHED);
        }
        if (!auction.isHighestBidder(buyer)) {
            throw new PurchaseException(BUYER_IS_NOT_HIGHEST_BIDDER);
        }
        validateArtIsOnSale(art);
    }

    public void checkGeneralArt(
            final Art art,
            final Member buyer
    ) {
        if (art.isOwner(buyer)) {
            throw new PurchaseException(ART_OWNER_CANNOT_PURCHASE_OWN);
        }
        validateArtIsOnSale(art);
    }

    private void validateArtIsOnSale(final Art art) {
        if (art.isSold()) {
            throw new PurchaseException(ALREADY_SOLD);
        }
    }
}

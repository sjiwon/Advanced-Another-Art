package com.sjiwon.anotherart.auction.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.ART_OWNER_CANNOT_BID;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.AUCTION_IS_NOT_IN_PROGRESS;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.BID_PRICE_IS_NOT_ENOUGH;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN;

@Service
@RequiredArgsConstructor
public class BidInspector {
    public void checkBidCanBeProceed(
            final Auction auction,
            final Art art,
            final Member bidder,
            final int newBidPrice
    ) {
        validateBidderIsOwner(art, bidder);
        validateAuctionIsOpen(auction);
        validateBidderIsCurrentHighestBidder(auction, bidder);
        validateNewBidPrice(auction, newBidPrice);
    }

    private void validateBidderIsOwner(final Art art, final Member bidder) {
        if (art.isOwner(bidder)) {
            throw new AuctionException(ART_OWNER_CANNOT_BID);
        }
    }

    private void validateAuctionIsOpen(final Auction auction) {
        if (!auction.isInProgress()) {
            throw new AuctionException(AUCTION_IS_NOT_IN_PROGRESS);
        }
    }

    private void validateBidderIsCurrentHighestBidder(final Auction auction, final Member bidder) {
        if (auction.isHighestBidder(bidder)) {
            throw new AuctionException(HIGHEST_BIDDER_CANNOT_BID_AGAIN);
        }
    }

    private void validateNewBidPrice(final Auction auction, final int newBidPrice) {
        if (!auction.isNewBidPriceAcceptable(newBidPrice)) {
            throw new AuctionException(BID_PRICE_IS_NOT_ENOUGH);
        }
    }
}

package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CurrentHighestBidder {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", referencedColumnName = "id", updatable = false)
    private Member bidder;

    @Column(name = "bid_amount")
    private int bidAmount;

    private CurrentHighestBidder(Member bidder, int bidAmount) {
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }

    public static CurrentHighestBidder of(Member bidder, int bidAmount) {
        return new CurrentHighestBidder(bidder, bidAmount);
    }

    public CurrentHighestBidder applyNewBid(Member newBidder, int newBidAmount) {
        verifyBidPrice(newBidAmount);
        verifyDuplicateBid(newBidder.getId());
        proceedingPointTransaction(newBidder, newBidAmount);
        return new CurrentHighestBidder(newBidder, newBidAmount);
    }

    private void verifyBidPrice(int newBidAmount) {
        if (this.bidAmount >= newBidAmount) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_BID_PRICE);
        }
    }

    private void verifyDuplicateBid(Long newBidderId) {
        if (isBidderExists() && Objects.equals(this.getBidder().getId(), newBidderId)) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_DUPLICATE_BID);
        }
    }

    private void proceedingPointTransaction(Member newBidder, int newBidAmount) {
        if (isBidderExists()) {
            this.bidder.increasePoint(this.bidAmount);
        }
        newBidder.decreasePoint(newBidAmount);
    }

    public boolean isBidderExists() {
        return this.bidder != null;
    }
}

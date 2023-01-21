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
    @JoinColumn(name = "member_id", referencedColumnName = "id", updatable = false)
    private Member member;

    @Column(name = "bid_price")
    private int bidPrice;

    private CurrentHighestBidder(Member member, int bidPrice) {
        this.member = member;
        this.bidPrice = bidPrice;
    }

    public static CurrentHighestBidder of(Member member, int bidPrice) {
        return new CurrentHighestBidder(member, bidPrice);
    }

    public CurrentHighestBidder applyNewBid(Member newBidMember, int newBidPrice) {
        verifyBidPrice(newBidPrice);
        verifyDuplicateBid(newBidMember.getId());
        proceedingPointTransaction(newBidMember, newBidPrice);
        return new CurrentHighestBidder(newBidMember, newBidPrice);
    }

    private void verifyBidPrice(int newBidPrice) {
        if (this.bidPrice >= newBidPrice) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_BID_PRICE);
        }
    }

    private void verifyDuplicateBid(Long newBidMemberId) {
        if (isBidderExists() && Objects.equals(this.getMember().getId(), newBidMemberId)) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_DUPLICATE_BID);
        }
    }

    private void proceedingPointTransaction(Member newBidMember, int newBidPrice) {
        if (isBidderExists()) {
            this.member.increasePoint(this.bidPrice);
        }
        newBidMember.decreasePoint(newBidPrice);
    }

    public boolean isBidderExists() {
        return this.member != null;
    }
}

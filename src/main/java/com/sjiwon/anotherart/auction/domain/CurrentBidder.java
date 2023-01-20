package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CurrentBidder {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", updatable = false)
    private Member member;

    @Column(name = "bid_price")
    private int bidPrice;

    @CreatedDate
    @Column(name = "bid_date")
    private LocalDateTime bidDate;

    private CurrentBidder(Member member, int bidPrice) {
        this.member = member;
        this.bidPrice = bidPrice;
    }

    public static CurrentBidder of(Member member, int bidPrice) {
        return new CurrentBidder(member, bidPrice);
    }

    public CurrentBidder applyNewBid(Member member, int bidPrice) {
        verifyDuplicateBid(member.getId());
        verifyBidPrice(bidPrice);
        return new CurrentBidder(member, bidPrice);
    }

    private void verifyDuplicateBid(Long currentBidMemberId) {
        if (Objects.equals(this.getMember().getId(), currentBidMemberId)) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_DUPLICATE_BID);
        }
    }

    private void verifyBidPrice(int bidPrice) {
        if (this.bidPrice >= bidPrice) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_BID_PRICE);
        }
    }
}

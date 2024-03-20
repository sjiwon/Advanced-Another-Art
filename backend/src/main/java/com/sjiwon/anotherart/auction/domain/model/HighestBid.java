package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.BID_PRICE_IS_NOT_ENOUGH;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class HighestBid {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id", referencedColumnName = "id")
    private Member bidder;

    @Column(name = "highest_bid_price")
    private int bidPrice;

    private HighestBid(final Member bidder, final int bidPrice) {
        this.bidder = bidder;
        this.bidPrice = bidPrice;
    }

    public static HighestBid init(final int initPrice) {
        return new HighestBid(null, initPrice);
    }

    public void applyNewBid(final Member newBidder, final int newBidPrice) {
        validateNewBidderIsCurrentHighestBidder(newBidder);
        validateNewBidPriceIsSuitable(newBidPrice);
        updateHighestBid(newBidder, newBidPrice);
    }

    private void validateNewBidderIsCurrentHighestBidder(final Member newBidder) {
        if (isBidderExists() && bidder.isSame(newBidder)) {
            throw new AuctionException(HIGHEST_BIDDER_CANNOT_BID_AGAIN);
        }
    }

    private void validateNewBidPriceIsSuitable(final int newBidPrice) {
        if (isBidderExists()) {
            if (bidPrice >= newBidPrice) {
                throw new AuctionException(BID_PRICE_IS_NOT_ENOUGH);
            }
        } else {
            if (bidPrice > newBidPrice) {
                throw new AuctionException(BID_PRICE_IS_NOT_ENOUGH);
            }
        }
    }

    private void updateHighestBid(final Member newBidder, final int newBidPrice) {
        // 1. 이전 최고 입찰자 = 사용 가능한 포인트 반환
        if (isBidderExists()) {
            bidder.increaseAvailablePoint(bidPrice);
        }

        // 2. 새로운 입찰자 = 사용 가능한 포인트 적용
        newBidder.decreaseAvailablePoint(newBidPrice);

        // 3. 최고 입찰자 교체
        bidder = newBidder;
        bidPrice = newBidPrice;
    }

    private boolean isBidderExists() {
        return bidder != null;
    }
}

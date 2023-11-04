package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Bidders {
    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private final List<AuctionRecord> auctionRecords = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id", referencedColumnName = "id")
    private Member highestBidder;

    @Column(name = "highest_bid_price")
    private int highestBidPrice;

    private Bidders(final Member highestBidder, final int highestBidPrice) {
        this.highestBidder = highestBidder;
        this.highestBidPrice = highestBidPrice;
    }

    public static Bidders init(final int highestBidPrice) {
        return new Bidders(null, highestBidPrice);
    }

    public void applyNewBid(final Auction auction, final Member bidder, final int bidPrice) {
        validateCurrentBidderIsHighestBidder(bidder);
        validateNewBidHigherThanCurrentHighestBid(bidPrice);
        applyHighestBidAndUpdateAvailablePointsForBidders(bidder, bidPrice);

        auctionRecords.add(AuctionRecord.createAuctionRecord(auction, bidder, bidPrice));
    }

    private void validateCurrentBidderIsHighestBidder(final Member bidder) {
        if (isBidderExists() && highestBidder.isSameMember(bidder)) {
            throw AnotherArtException.type(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN);
        }
    }

    private void validateNewBidHigherThanCurrentHighestBid(final int bidPrice) {
        if (isBidderExists()) {
            if (highestBidPrice >= bidPrice) {
                throw AnotherArtException.type(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH);
            }
        } else {
            if (highestBidPrice > bidPrice) {
                throw AnotherArtException.type(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH);
            }
        }
    }

    private boolean isBidderExists() {
        return highestBidder != null;
    }

    private void applyHighestBidAndUpdateAvailablePointsForBidders(final Member bidder, final int bidPrice) {
        if (isBidderExists()) {
            highestBidder.increaseAvailablePoint(highestBidPrice);
        }
        bidder.decreaseAvailablePoint(bidPrice);

        highestBidder = bidder;
        highestBidPrice = bidPrice;
    }
}

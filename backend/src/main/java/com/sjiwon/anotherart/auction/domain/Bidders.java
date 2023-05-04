package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Bidders {
    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private List<AuctionRecord> auctionRecords = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "highest_bidder_id", referencedColumnName = "id")
    private Member highestBidder;

    @Column(name = "highest_bid_price")
    private int highestBidPrice;

    private Bidders(Member highestBidder, int highestBidPrice) {
        this.highestBidder = highestBidder;
        this.highestBidPrice = highestBidPrice;
    }

    public static Bidders init(int highestBidPrice) {
        return new Bidders(null, highestBidPrice);
    }

    public void applyNewBid(Auction auction, Member bidder, int bidPrice) {
        validateCurrentBidderIsHighestBidder(bidder);
        validateNewBidHigherThanCurrentHighestBid(bidPrice);
        applyHighestBidAndupdateAvailablePointsForBidders(bidder, bidPrice);

        auctionRecords.add(AuctionRecord.createAuctionRecord(auction, bidder, bidPrice));
    }

    private void validateCurrentBidderIsHighestBidder(Member bidder) {
        if (isBidderExists() && highestBidder.isSameMember(bidder.getId())) {
            throw AnotherArtException.type(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN);
        }
    }

    private void validateNewBidHigherThanCurrentHighestBid(int bidPrice) {
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

    private void applyHighestBidAndupdateAvailablePointsForBidders(Member bidder, int bidPrice) {
        if (isBidderExists()) {
            highestBidder.increaseAvailablePoint(highestBidPrice);
        }
        bidder.decreaseAvailablePoint(bidPrice);

        highestBidder = bidder;
        highestBidPrice = bidPrice;
    }
}

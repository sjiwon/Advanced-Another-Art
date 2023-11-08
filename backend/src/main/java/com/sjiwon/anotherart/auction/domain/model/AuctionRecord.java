package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auction_record")
public class AuctionRecord extends BaseEntity<AuctionRecord> {
    @Column(name = "bid_price")
    private int bidPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", referencedColumnName = "id", updatable = false)
    private Member bidder;

    private AuctionRecord(final Auction auction, final Member bidder, final int bidPrice) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = bidPrice;
    }

    public static AuctionRecord createAuctionRecord(final Auction auction, final Member bidder, final int bidPrice) {
        return new AuctionRecord(auction, bidder, bidPrice);
    }
}

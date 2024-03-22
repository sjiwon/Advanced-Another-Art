package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "auction_record")
public class AuctionRecord extends BaseEntity<AuctionRecord> {
    @Column(name = "bidder_id", updatable = false)
    private Long bidderId;

    @Column(name = "bid_price")
    private int bidPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Auction auction;

    public AuctionRecord(final Auction auction, final Member bidder, final int bidPrice) {
        this.auction = auction;
        this.bidderId = bidder.getId();
        this.bidPrice = bidPrice;
        auction.getAuctionRecords().add(this);
    }
}

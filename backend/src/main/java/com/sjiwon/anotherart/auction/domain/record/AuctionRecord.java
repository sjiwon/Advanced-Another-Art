package com.sjiwon.anotherart.auction.domain.record;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auction_record")
public class AuctionRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bid_price")
    private int bidPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", referencedColumnName = "id", updatable = false)
    private Member bidder;

    private AuctionRecord(Auction auction, Member bidder, int bidPrice) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidPrice = bidPrice;
    }

    public static AuctionRecord createAuctionRecord(Auction auction, Member bidder, int bidPrice) {
        return new AuctionRecord(auction, bidder, bidPrice);
    }
}

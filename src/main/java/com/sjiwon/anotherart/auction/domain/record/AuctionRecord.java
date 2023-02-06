package com.sjiwon.anotherart.auction.domain.record;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auction_record")
@EntityListeners(AuditingEntityListener.class)
public class AuctionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bid_amount")
    private int bidAmount;

    @CreatedDate
    @Column(name = "bid_date")
    private LocalDateTime bidDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Auction auction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", referencedColumnName = "id", updatable = false)
    private Member bidder;

    @Builder
    private AuctionRecord(Auction auction, Member bidder, int bidAmount) {
        this.auction = auction;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
        this.auction.getAuctionRecords().add(this);
    }

    public static AuctionRecord createAuctionRecord(Auction auction, Member bidder, int bidPrice) {
        return new AuctionRecord(auction, bidder, bidPrice);
    }
}

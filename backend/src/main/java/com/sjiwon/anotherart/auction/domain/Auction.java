package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auction")
@EntityListeners(AuditingEntityListener.class)
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Period period;

    @Column(name = "bid_amount")
    private int bidAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", referencedColumnName = "id")
    private Member bidder;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Art art;

    @Builder
    private Auction(Art art, Period period) {
        this.art = art;
        this.period = period;
        this.bidder = null;
        this.bidAmount = art.getPrice();
    }

    public static Auction initAuction(Art art, Period period) {
        validateArtType(art);
        return new Auction(art, period);
    }

    private static void validateArtType(Art art) {
        if (!art.isAuctionType()) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_ART_TYPE);
        }
    }

    public void applyNewBid(Member newBidder, int newBidAmount) {
        proceedingPointTransaction(newBidder, newBidAmount);
        this.bidder = newBidder;
        this.bidAmount = newBidAmount;
    }

    private void proceedingPointTransaction(Member newBidder, int newBidAmount) {
        if (isBidderExists()) {
            this.bidder.increasePoint(this.bidAmount);
        }
        newBidder.decreasePoint(newBidAmount);
    }

    public boolean isAuctionInProgress() {
        return this.period.isAuctionInProgress(LocalDateTime.now());
    }

    public boolean isBidderExists() {
        return this.bidder != null;
    }

    // Generate Getter
    public LocalDateTime getAuctionStartDate() {
        return period.getStartDate();
    }

    public LocalDateTime getAuctionEndDate() {
        return period.getEndDate();
    }
}

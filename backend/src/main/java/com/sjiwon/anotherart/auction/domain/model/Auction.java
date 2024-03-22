package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.INVALID_ART_TYPE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity<Auction> {
    @Column(name = "art_id", nullable = false, updatable = false, unique = true)
    private Long artId;

    @Embedded
    private Period period;

    @Column(name = "highest_bidder_id")
    private Long highestBidderId;

    @Column(name = "highest_bid_price", nullable = false)
    private int highestBidPrice;

    @Version
    private long version;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private final List<AuctionRecord> auctionRecords = new ArrayList<>();

    private Auction(final Art art, final Period period) {
        this.artId = art.getId();
        this.period = period;
        this.highestBidPrice = art.getPrice();
    }

    public static Auction createAuction(final Art art, final Period period) {
        if (!art.isAuctionType()) {
            throw new AuctionException(INVALID_ART_TYPE);
        }

        return new Auction(art, period);
    }

    public boolean isInProgress() {
        return period.isDateWithInRange(LocalDateTime.now());
    }

    public boolean isHighestBidder(final Member other) {
        if (highestBidderId == null) {
            return false;
        }
        return highestBidderId.equals(other.getId());
    }

    public boolean isNewBidPriceAcceptable(final int newBidPrice) {
        if (isBidderExists()) {
            return highestBidPrice < newBidPrice;
        }
        return highestBidPrice <= newBidPrice;
    }

    private boolean isBidderExists() {
        return highestBidderId != null;
    }

    public void updateHighestBid(
            final Member newBidder,
            final int newBidPrice
    ) {
        this.highestBidderId = newBidder.getId();
        this.highestBidPrice = newBidPrice;
    }
}

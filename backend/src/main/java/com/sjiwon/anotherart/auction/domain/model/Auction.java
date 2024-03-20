package com.sjiwon.anotherart.auction.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.ART_OWNER_CANNOT_BID;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.AUCTION_IS_NOT_IN_PROGRESS;
import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.INVALID_ART_TYPE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity<Auction> {
    @Embedded
    private Period period;

    @Embedded
    private HighestBid highestBid;

    @Version
    private long version;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Art art;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.PERSIST)
    private final List<AuctionRecord> auctionRecords = new ArrayList<>();

    private Auction(final Art art, final Period period) {
        this.art = art;
        this.period = period;
        this.highestBid = HighestBid.init(art.getPrice());
    }

    public static Auction createAuction(final Art art, final Period period) {
        validateArtType(art);
        return new Auction(art, period);
    }

    private static void validateArtType(final Art art) {
        if (!art.isAuctionType()) {
            throw new AuctionException(INVALID_ART_TYPE);
        }
    }

    public void applyNewBid(final Member newBidder, final int newBidPrice) {
        validateAuctionIsOpen();
        validateArtOwner(newBidder);
        highestBid.applyNewBid(newBidder, newBidPrice);
        auctionRecords.add(AuctionRecord.createAuctionRecord(this, newBidder, newBidPrice));
    }

    private void validateAuctionIsOpen() {
        final LocalDateTime now = LocalDateTime.now();

        if (!period.isDateWithInRange(now)) {
            throw new AuctionException(AUCTION_IS_NOT_IN_PROGRESS);
        }
    }

    private void validateArtOwner(final Member bidder) {
        if (art.isOwner(bidder)) {
            throw new AuctionException(ART_OWNER_CANNOT_BID);
        }
    }

    public boolean isHighestBidder(final Member other) {
        return highestBid.getBidder().isSame(other);
    }

    public boolean isInProgress() {
        return period.isDateWithInRange(LocalDateTime.now());
    }

    // Add Getter
    public Member getHighestBidder() {
        return highestBid.getBidder();
    }

    public int getHighestBidPrice() {
        return highestBid.getBidPrice();
    }
}

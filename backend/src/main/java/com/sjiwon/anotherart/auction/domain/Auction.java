package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity<Auction> {
    @Embedded
    private Period period;

    @Embedded
    private Bidders bidders;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Art art;

    private Auction(final Art art, final Period period) {
        this.art = art;
        this.period = period;
        this.bidders = Bidders.init(art.getPrice());
    }

    public static Auction createAuction(final Art art, final Period period) {
        validateArtType(art);
        return new Auction(art, period);
    }

    private static void validateArtType(final Art art) {
        if (!art.isAuctionType()) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_ART_TYPE);
        }
    }

    public void applyNewBid(final Member bidder, final int bidPrice) {
        validateAuctionIsOpen();
        validateArtOwner(bidder);

        this.bidders.applyNewBid(this, bidder, bidPrice);
    }

    private void validateAuctionIsOpen() {
        final LocalDateTime now = LocalDateTime.now();

        if (!period.isDateWithInRange(now)) {
            throw AnotherArtException.type(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS);
        }
    }

    private void validateArtOwner(final Member bidder) {
        if (art.isArtOwner(bidder)) {
            throw AnotherArtException.type(AuctionErrorCode.ART_OWNER_CANNOT_BID);
        }
    }

    public boolean isFinished() {
        return period.isAuctionFinished(LocalDateTime.now());
    }

    public boolean isHighestBidder(final Member other) {
        return getHighestBidder().isSameMember(other);
    }

    // Add Getter
    public List<AuctionRecord> getAuctionRecords() {
        return bidders.getAuctionRecords();
    }

    public Member getHighestBidder() {
        return bidders.getHighestBidder();
    }

    public int getHighestBidPrice() {
        return bidders.getHighestBidPrice();
    }
}

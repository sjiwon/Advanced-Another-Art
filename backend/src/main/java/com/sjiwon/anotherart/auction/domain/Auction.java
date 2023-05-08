package com.sjiwon.anotherart.auction.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "auction")
public class Auction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Period period;

    @Embedded
    private Bidders bidders;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Art art;

    private Auction(Art art, Period period) {
        this.art = art;
        this.period = period;
        this.bidders = Bidders.init(art.getPrice());
    }

    public static Auction createAuction(Art art, Period period) {
        validateArtType(art);
        return new Auction(art, period);
    }

    private static void validateArtType(Art art) {
        if (!art.isAuctionType()) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_ART_TYPE);
        }
    }

    public void applyNewBid(Member bidder, int bidPrice) {
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

    private void validateArtOwner(Member bidder) {
        if (art.isArtOwner(bidder)) {
            throw AnotherArtException.type(AuctionErrorCode.ART_OWNER_CANNOT_BID);
        }
    }

    public boolean isFinished() {
        return period.isAuctionFinished(LocalDateTime.now());
    }

    public boolean isHighestBidder(Member other) {
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

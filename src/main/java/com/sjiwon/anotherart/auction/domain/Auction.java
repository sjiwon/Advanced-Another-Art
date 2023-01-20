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
import java.util.Objects;

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

    @Embedded
    private CurrentBidder currentBidder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Art art;

    @Builder
    private Auction(Art art, CurrentBidder currentBidder, Period period) {
        this.art = art;
        this.currentBidder = currentBidder;
        this.period = period;
    }

    public static Auction initAuction(Art art, Period period) {
        validateArtType(art);
        CurrentBidder currentBidder = CurrentBidder.of(art.getMember(), art.getPrice());
        return new Auction(art, currentBidder, period);
    }

    private static void validateArtType(Art art) {
        if (art.isNotAuctionType()) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_ART_TYPE);
        }
    }

    public Auction applyNewBid(Member member, int bidPrice) {
        verifyArtOwnerBid(member.getId());
        return new Auction(this.art, this.currentBidder.applyNewBid(member, bidPrice), this.period);
    }

    private void verifyArtOwnerBid(Long currentBidMemberId) {
        if (Objects.equals(this.art.getMember().getId(), currentBidMemberId)) {
            throw AnotherArtException.type(AuctionErrorCode.INVALID_OWNER_BID);
        }
    }
}

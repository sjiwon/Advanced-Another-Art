package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.domain.Description;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AuctionArt implements ArtDetails {
    private final BasicAuction auction;
    private final BasicArt art;
//    private final BasicMember owner;
//    private final BasicMember highestBidder;

    @QueryProjection
    public AuctionArt(
            final Long auctionId, final Integer highestBidPrice, final LocalDateTime startDate, final LocalDateTime endDate,
            final Long artId, final ArtName artName, final Description artDescription, final int price, final ArtStatus status, final String storageName, final LocalDateTime registrationDate,
            final Long ownerId, final Nickname ownerNickname, final String ownerSchool,
            final Long highestBidderId, final Nickname highestBidderNickname, final String highestBidderSchool
    ) {
        this.auction = new BasicAuction(
                auctionId,
                highestBidPrice,
                startDate,
                endDate
        );

        this.art = new BasicArt(
                artId,
                artName,
                artDescription,
                price,
                status,
                storageName,
                registrationDate
        );

//        this.owner = new BasicMember(
//                ownerId,
//                ownerNickname,
//                ownerSchool
//        );

//        this.highestBidder = highestBidderId != null
//                ? new BasicMember(highestBidderId, highestBidderNickname, highestBidderSchool)
//                : null;
    }

    public void applyHashtags(final List<String> hashtags) {
        this.art.applyHashtags(hashtags);
    }

    public void applyLikeMembers(final List<Long> likeMembers) {
        this.art.applyLikeMembers(likeMembers);
    }

    public void applyBidCount(final int bidCount) {
        this.auction.applyBidCount(bidCount);
    }
}

package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.domain.Description;
import com.sjiwon.anotherart.member.domain.Nickname;
import com.sjiwon.anotherart.member.infra.query.dto.response.BasicMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AuctionArt implements ArtDetails {
    private final BasicAuction auction;
    private final BasicArt art;
    private final BasicMember owner;
    private final BasicMember highestBidder;

    @QueryProjection
    public AuctionArt(
            Long auctionId, Integer highestBidPrice, LocalDateTime startDate, LocalDateTime endDate,
            Long artId, ArtName artName, Description artDescription, int price, ArtStatus status, String storageName, LocalDateTime registrationDate,
            Long ownerId, Nickname ownerNickname, String ownerSchool,
            Long highestBidderId, Nickname highestBidderNickname, String highestBidderSchool
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

        this.owner = new BasicMember(
                ownerId,
                ownerNickname,
                ownerSchool
        );

        this.highestBidder = highestBidderId != null
                ? new BasicMember(highestBidderId, highestBidderNickname, highestBidderSchool)
                : null;
    }

    public void applyHashtags(List<String> hashtags) {
        this.art.applyHashtags(hashtags);
    }

    public void applyBidCount(int bidCount) {
        this.auction.applyBidCount(bidCount);
    }
}

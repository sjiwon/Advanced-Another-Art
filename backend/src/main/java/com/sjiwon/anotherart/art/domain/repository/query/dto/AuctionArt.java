package com.sjiwon.anotherart.art.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.ArtStatus;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AuctionArt implements ArtDetails {
    // Auction
    private final Long auctionId;
    private final int highestBidPrice;
    private final LocalDateTime auctionStartDate;
    private final LocalDateTime auctionEndDate;
    private int bidCount;

    // Art
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final int artPrice;
    private final String artStatus;
    private final String artStorageUrl;
    private final LocalDateTime artRegistrationDate;
    private List<String> hashtags;
    private List<Long> likeMembers;

    // Owner
    private final Long ownerId;
    private final String ownerNickname;
    private final String ownerSchool;

    // Highest Bidder
    private final Long highestBidderId;
    private final String highestBidderNickname;
    private final String highestBidderSchool;

    @QueryProjection
    public AuctionArt(
            final Long auctionId,
            final Integer highestBidPrice,
            final LocalDateTime auctionStartDate,
            final LocalDateTime auctionEndDate,
            final Long artId,
            final ArtName artName,
            final Description artDescription,
            final int artPrice,
            final ArtStatus artStatus,
            final UploadImage uploadImage,
            final LocalDateTime artRegistrationDate,
            final Long ownerId,
            final Nickname ownerNickname,
            final String ownerSchool,
            final Long highestBidderId,
            final Nickname highestBidderNickname,
            final String highestBidderSchool
    ) {
        this.auctionId = auctionId;
        this.highestBidPrice = highestBidPrice;
        this.auctionStartDate = auctionStartDate;
        this.auctionEndDate = auctionEndDate;
        this.artId = artId;
        this.artName = artName.getValue();
        this.artDescription = artDescription.getValue();
        this.artPrice = artPrice;
        this.artStatus = artStatus.getDescription();
        this.artStorageUrl = uploadImage.getLink();
        this.artRegistrationDate = artRegistrationDate;
        this.ownerId = ownerId;
        this.ownerNickname = ownerNickname.getValue();
        this.ownerSchool = ownerSchool;
        this.highestBidderId = highestBidderId;
        this.highestBidderNickname = highestBidderNickname.getValue();
        this.highestBidderSchool = highestBidderSchool;
    }

    public void applyHashtags(final List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyLikeMembers(final List<Long> likeMembers) {
        this.likeMembers = likeMembers;
    }

    public void applyBidCount(final int bidCount) {
        this.bidCount = bidCount;
    }
}

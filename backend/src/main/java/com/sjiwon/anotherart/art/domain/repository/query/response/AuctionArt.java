package com.sjiwon.anotherart.art.domain.repository.query.response;

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

    public AuctionArt(
            final Long auctionId,
            final Integer highestBidPrice,
            final LocalDateTime auctionStartDate,
            final LocalDateTime auctionEndDate,
            final Long artId,
            final String artName,
            final String artDescription,
            final int artPrice,
            final String artStatus,
            final String artStorageUrl,
            final LocalDateTime artRegistrationDate,
            final Long ownerId,
            final String ownerNickname,
            final String ownerSchool,
            final Long highestBidderId,
            final String highestBidderNickname,
            final String highestBidderSchool
    ) {
        this.auctionId = auctionId;
        this.highestBidPrice = highestBidPrice;
        this.auctionStartDate = auctionStartDate;
        this.auctionEndDate = auctionEndDate;
        this.artId = artId;
        this.artName = artName;
        this.artDescription = artDescription;
        this.artPrice = artPrice;
        this.artStatus = artStatus;
        this.artStorageUrl = artStorageUrl;
        this.artRegistrationDate = artRegistrationDate;
        this.ownerId = ownerId;
        this.ownerNickname = ownerNickname;
        this.ownerSchool = ownerSchool;

        if (highestBidderId != null) {
            this.highestBidderId = highestBidderId;
            this.highestBidderNickname = highestBidderNickname;
            this.highestBidderSchool = highestBidderSchool;
        } else {
            this.highestBidderId = null;
            this.highestBidderNickname = null;
            this.highestBidderSchool = null;
        }
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

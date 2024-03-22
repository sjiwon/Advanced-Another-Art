package com.sjiwon.anotherart.art.domain.repository.query.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GeneralArt implements ArtDetails {
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

    // Buyer
    private final Long buyerId;
    private final String buyerNickname;
    private final String buyerSchool;

    public GeneralArt(
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
            final Long buyerId,
            final String buyerNickname,
            final String buyerSchool
    ) {
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

        if (buyerId != null) {
            this.buyerId = buyerId;
            this.buyerNickname = buyerNickname;
            this.buyerSchool = buyerSchool;
        } else {
            this.buyerId = null;
            this.buyerNickname = null;
            this.buyerSchool = null;
        }
    }

    public void applyHashtags(final List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyLikeMembers(final List<Long> likeMembers) {
        this.likeMembers = likeMembers;
    }
}

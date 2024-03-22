package com.sjiwon.anotherart.art.domain.repository.query.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.member.domain.model.Nickname;
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

    @QueryProjection
    public GeneralArt(
            final Long artId,
            final ArtName artName,
            final Description artDescription,
            final int artPrice,
            final Art.Status artStatus,
            final UploadImage uploadImage,
            final LocalDateTime artRegistrationDate,
            final Long ownerId,
            final Nickname ownerNickname,
            final String ownerSchool,
            final Long buyerId,
            final Nickname buyerNickname,
            final String buyerSchool
    ) {
        this.artId = artId;
        this.artName = artName.getValue();
        this.artDescription = artDescription.getValue();
        this.artPrice = artPrice;
        this.artStatus = artStatus.name();
        this.artStorageUrl = uploadImage.getLink();
        this.artRegistrationDate = artRegistrationDate;
        this.ownerId = ownerId;
        this.ownerNickname = ownerNickname.getValue();
        this.ownerSchool = ownerSchool;

        if (buyerId != null) {
            this.buyerId = buyerId;
            this.buyerNickname = buyerNickname.getValue();
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

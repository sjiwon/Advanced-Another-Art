package com.sjiwon.anotherart.member.domain.repository.query.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class WinningAuctionArt {
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final String artStorageUrl;
    private final String ownerNickname;
    private final String ownerSchool;
    private final int highestBidPrice;
    private List<String> artHashtags;

    @QueryProjection
    public WinningAuctionArt(
            final Long artId,
            final ArtName artName,
            final Description artDescription,
            final UploadImage uploadImage,
            final Nickname ownerNickname,
            final String ownerSchool,
            final int highestBidPrice
    ) {
        this.artId = artId;
        this.artName = artName.getValue();
        this.artDescription = artDescription.getValue();
        this.artStorageUrl = uploadImage.getLink();
        this.ownerNickname = ownerNickname.getValue();
        this.ownerSchool = ownerSchool;
        this.highestBidPrice = highestBidPrice;
    }

    public void applyHashtags(final List<String> artHashtags) {
        this.artHashtags = artHashtags;
    }
}

package com.sjiwon.anotherart.member.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SoldArts {
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final String artStorageUrl;
    private final String buyerNickname;
    private final String buyerSchool;
    private final int soldPrice;
    private List<String> artHashtags;

    @QueryProjection
    public SoldArts(
            final Long artId,
            final ArtName artName,
            final Description artDescription,
            final String artStorageUrl,
            final Nickname buyerNickname,
            final String buyerSchool,
            final int soldPrice
    ) {
        this.artId = artId;
        this.artName = artName.getValue();
        this.artDescription = artDescription.getValue();
        this.artStorageUrl = artStorageUrl;
        this.buyerNickname = buyerNickname.getValue();
        this.buyerSchool = buyerSchool;
        this.soldPrice = soldPrice;
    }

    private void applyHashtags(final List<String> artHashtags) {
        this.artHashtags = artHashtags;
    }
}

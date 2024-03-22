package com.sjiwon.anotherart.member.domain.repository.query.response;

import lombok.Getter;

import java.util.List;

@Getter
public class SoldArt {
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final String artStorageUrl;
    private final String buyerNickname;
    private final String buyerSchool;
    private final int soldPrice;
    private List<String> artHashtags;

    public SoldArt(
            final Long artId,
            final String artName,
            final String artDescription,
            final String artStorageUrl,
            final String buyerNickname,
            final String buyerSchool,
            final int soldPrice
    ) {
        this.artId = artId;
        this.artName = artName;
        this.artDescription = artDescription;
        this.artStorageUrl = artStorageUrl;
        this.buyerNickname = buyerNickname;
        this.buyerSchool = buyerSchool;
        this.soldPrice = soldPrice;
    }

    public void applyHashtags(final List<String> artHashtags) {
        this.artHashtags = artHashtags;
    }
}

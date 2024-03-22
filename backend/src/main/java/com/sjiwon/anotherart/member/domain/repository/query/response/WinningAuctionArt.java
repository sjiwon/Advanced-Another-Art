package com.sjiwon.anotherart.member.domain.repository.query.response;

import lombok.Getter;

import java.util.List;

@Getter
public class WinningAuctionArt {
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final String artStorageUrl;
    private final String ownerNickname;
    private final String ownerSchool;
    private final int highestBidPrice;
    private List<String> artHashtags;

    public WinningAuctionArt(
            final Long artId,
            final String artName,
            final String artDescription,
            final String artStorageUrl,
            final String ownerNickname,
            final String ownerSchool,
            final int highestBidPrice
    ) {
        this.artId = artId;
        this.artName = artName;
        this.artDescription = artDescription;
        this.artStorageUrl = artStorageUrl;
        this.ownerNickname = ownerNickname;
        this.ownerSchool = ownerSchool;
        this.highestBidPrice = highestBidPrice;
    }

    public void applyHashtags(final List<String> artHashtags) {
        this.artHashtags = artHashtags;
    }
}

package com.sjiwon.anotherart.art.utils.search;

import com.sjiwon.anotherart.art.domain.model.ArtType;

import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;

public record ArtDetailSearchCondition(
        SortType sortType,
        ArtType artType,
        String value
) {
    public ArtDetailSearchCondition(final String sortType, final String artType, final String value) {
        this(
                SortType.from(sortType),
                artType.equals("general") ? GENERAL : AUCTION,
                value
        );
    }

    public boolean isAuctionType() {
        return artType == AUCTION;
    }
}

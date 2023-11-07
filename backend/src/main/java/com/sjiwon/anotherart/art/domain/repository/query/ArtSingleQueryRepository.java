package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;

public interface ArtSingleQueryRepository {
    ArtType getArtType(final Long artId);

    AuctionArt fetchAuctionArt(final Long artId);

    GeneralArt fetchGeneralArt(final Long artId);
}

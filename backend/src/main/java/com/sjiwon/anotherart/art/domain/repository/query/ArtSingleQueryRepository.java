package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;

public interface ArtSingleQueryRepository {
    Art.Type getArtType(final Long artId);

    AuctionArt fetchAuctionArt(final Long artId);

    GeneralArt fetchGeneralArt(final Long artId);
}

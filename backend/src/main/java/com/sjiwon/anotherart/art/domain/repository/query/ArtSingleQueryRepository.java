package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.response.GeneralArt;

public interface ArtSingleQueryRepository {
    AuctionArt getAuctionArt(Long artId);

    GeneralArt getGeneralArt(Long artId);
}

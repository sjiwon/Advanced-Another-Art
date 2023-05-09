package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;

public interface ArtSingleQueryRepository {
    AuctionArt getAuctionArt(Long artId);
    GeneralArt getGeneralArt(Long artId);
}

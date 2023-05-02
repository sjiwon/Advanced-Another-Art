package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;

import java.util.List;

public interface ArtSpecificSimpleQueryRepository {
    List<String> getHashtagsById(Long artId);
    List<Long> getLikeMarkingMembersById(Long artId);
    BasicGeneralArt getGeneralArtById(Long artId);
    BasicAuctionArt getAuctionArtById(Long artId);
}

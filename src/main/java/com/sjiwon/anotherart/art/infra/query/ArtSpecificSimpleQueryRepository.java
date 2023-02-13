package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.response.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicGeneralArt;

import java.util.List;

public interface ArtSpecificSimpleQueryRepository {
    List<String> getHashtagsById(Long artId);
    List<Long> getLikeMarkingMembersById(Long artId);
    BasicGeneralArt findGeneralArtById(Long artId);
    BasicAuctionArt findAuctionArtById(Long artId);
}

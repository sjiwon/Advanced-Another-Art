package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.art.utils.search.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtDetailQueryRepository {
    Page<AuctionArt> findActiveAuctionArts(SortType sortType, Pageable pageable);
    Page<AuctionArt> findAuctionArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable);
    Page<AuctionArt> findAuctionArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable);
    Page<GeneralArt> findGeneralArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable);
    Page<GeneralArt> findGeneralArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable);
}

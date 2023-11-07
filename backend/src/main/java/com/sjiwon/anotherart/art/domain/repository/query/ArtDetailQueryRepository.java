package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.art.utils.search.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.ArtDetailsSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtDetailQueryRepository {
    Page<AuctionArt> fetchActiveAuctionArts(final ActiveAuctionArtsSearchCondition condition, final Pageable pageable);

    Page<AuctionArt> fetchAuctionArtsByKeyword(final ArtDetailsSearchCondition condition, final Pageable pageable);

    Page<AuctionArt> fetchAuctionArtsByHashtag(final ArtDetailsSearchCondition condition, final Pageable pageable);

    Page<GeneralArt> fetchGeneralArtsByKeyword(final ArtDetailsSearchCondition condition, final Pageable pageable);

    Page<GeneralArt> fetchGeneralArtsByHashtag(final ArtDetailsSearchCondition condition, final Pageable pageable);
}

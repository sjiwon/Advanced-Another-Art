package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.art.utils.search.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class ArtDetailQueryRepositoryImpl implements ArtDetailQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public Page<AuctionArt> fetchActiveAuctionArts(
            final ActiveAuctionArtsSearchCondition condition,
            final Pageable pageable
    ) {
        return null;
    }

    @Override
    public Page<AuctionArt> fetchAuctionArtsByKeyword(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        return null;
    }

    @Override
    public Page<GeneralArt> fetchGeneralArtsByKeyword(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        return null;
    }

    @Override
    public Page<AuctionArt> fetchAuctionArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        return null;
    }

    @Override
    public Page<GeneralArt> fetchGeneralArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        return null;
    }
}

package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// TODO 전체 Command 로직 리팩토링 후 Query 로직 한번에 구현
@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class ArtSingleQueryRepositoryImpl implements ArtSingleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public ArtType getArtType(final Long artId) {
        return null;
    }

    @Override
    public AuctionArt fetchAuctionArt(final Long artId) {
        return null;
    }

    @Override
    public GeneralArt fetchGeneralArt(final Long artId) {
        return null;
    }
}

package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sjiwon.anotherart.art.domain.model.QArt.art;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class ArtSingleQueryRepositoryImpl implements ArtSingleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public ArtType getArtType(final Long artId) {
        return query
                .select(art.type)
                .from(art)
                .where(art.id.eq(artId))
                .fetchOne();
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

package com.sjiwon.anotherart.auction.infra;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionRecordQueryDslRepositoryImpl implements AuctionRecordQueryDslRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean existsAuctionRecordByArtId(Long artId) {
        Integer count = query
                .selectOne()
                .from(auctionRecord)
                .innerJoin(auctionRecord.auction, auction)
                .where(artIdEq(artId))
                .fetchFirst();

        return count != null;
    }

    private BooleanExpression artIdEq(Long artId) {
        return (artId != null) ? auction.art.id.eq(artId) : null;
    }
}

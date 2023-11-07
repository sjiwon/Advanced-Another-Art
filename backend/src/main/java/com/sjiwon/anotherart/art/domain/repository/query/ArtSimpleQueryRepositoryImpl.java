package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtSimpleQueryRepositoryImpl implements ArtSimpleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public boolean isAuctionRecordExists(final Long artId) {
        final Integer count = query
                .selectOne()
                .from(auctionRecord)
                .innerJoin(auctionRecord.auction, auction)
                .where(auction.art.id.eq(artId))
                .fetchFirst();

        return count != null;
    }
}

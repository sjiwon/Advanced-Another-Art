package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.infra.query.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.hashtag.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtSummaryQueryRepositoryImpl implements ArtSummaryQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public List<HashtagSummary> findHashtagSummaryList() {
        return query
                .select(new QHashtagSummary(hashtag.id, hashtag.art.id, hashtag.name))
                .from(hashtag)
                .fetch();
    }

    @Override
    public List<FavoriteSummary> findFavoriteSummaryList() {
        return query
                .select(new QFavoriteSummary(favorite.id, favorite.artId, favorite.memberId))
                .from(favorite)
                .fetch();
    }

    @Override
    public List<AuctionRecordSummary> findAuctionRecordSummaryList() {
        return query
                .select(new QAuctionRecordSummary(auctionRecord.id, auction.art.id, auctionRecord.bidder.id))
                .from(auctionRecord)
                .innerJoin(auctionRecord.auction, auction)
                .fetch();
    }
}

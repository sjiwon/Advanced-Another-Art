package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionActivitySummary;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.FavoriteSummary;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.HashtagSummary;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QAuctionActivitySummary;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QAuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QFavoriteSummary;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QGeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QHashtagSummary;
import com.sjiwon.anotherart.art.utils.search.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.SearchSortType;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.member.domain.model.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.QArt.art;
import static com.sjiwon.anotherart.art.domain.model.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.model.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.model.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.model.QFavorite.favorite;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class ArtDetailQueryRepositoryImpl implements ArtDetailQueryRepository {
    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");
    private static final QMember highestBidder = new QMember("highestBidder");

    private final JPAQueryFactory query;

    @Override
    public Page<AuctionArt> fetchActiveAuctionArts(
            final ActiveAuctionArtsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<AuctionArt> result = projectionAuctionArts(
                condition.searchSortType(),
                pageable,
                Arrays.asList(auctionIsInProgress())
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(auctionIsInProgress())
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<AuctionArt> fetchAuctionArtsByKeyword(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<AuctionArt> result = projectionAuctionArts(
                condition.searchSortType(),
                pageable,
                Arrays.asList(artKeywordEq(condition.value()))
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(artKeywordEq(condition.value()))
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
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
        final List<Long> artIds = query
                .select(hashtag.art.id)
                .from(hashtag)
                .where(artHashtagEq(condition.value()))
                .fetch();
        final List<AuctionArt> result = projectionAuctionArts(
                condition.searchSortType(),
                pageable,
                Arrays.asList(artIdIn(artIds))
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(artIdIn(artIds))
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<GeneralArt> fetchGeneralArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        return null;
    }

    private List<AuctionArt> projectionAuctionArts(
            final SearchSortType sortType,
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final JPAQuery<AuctionArt> fetchQuery = query
                .select(fetchAuctionArt())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.highestBid.bidder, highestBidder)
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(orderBySearchCondition(sortType, AUCTION).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return completeAuctionArts(fetchQuery, sortType);
    }

    private ConstructorExpression<AuctionArt> fetchAuctionArt() {
        return new QAuctionArt(
                auction.id,
                auction.highestBid.bidPrice,
                auction.period.startDate,
                auction.period.endDate,
                art.id,
                art.name,
                art.description,
                art.price,
                art.status,
                art.uploadImage,
                art.createdAt,
                owner.id,
                owner.nickname,
                owner.school,
                highestBidder.id,
                highestBidder.nickname,
                highestBidder.school
        );
    }

    private ConstructorExpression<GeneralArt> fetchGeneralArt() {
        return new QGeneralArt(
                art.id,
                art.name,
                art.description,
                art.price,
                art.status,
                art.uploadImage,
                art.createdAt,
                owner.id,
                owner.nickname,
                owner.school,
                buyer.id,
                buyer.nickname,
                buyer.school
        );
    }

    private List<OrderSpecifier<?>> orderBySearchCondition(final SearchSortType sortType, final ArtType artType) {
        final List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sortType) {
            case DATE_ASC -> orderBy.add(art.id.asc());
            case DATE_DESC -> orderBy.add(art.id.desc());
            case PRICE_ASC -> orderBy.addAll(List.of(
                    artType == AUCTION ? auction.highestBid.bidPrice.asc() : art.price.asc(),
                    art.id.desc()
            ));
            case PRICE_DESC -> orderBy.addAll(List.of(
                    artType == AUCTION ? auction.highestBid.bidPrice.desc() : art.price.desc(),
                    art.id.desc()
            ));
            case LIKE_ASC -> orderBy.addAll(List.of(favorite.count().asc(), art.id.desc()));
            case LIKE_DESC -> orderBy.addAll(List.of(favorite.count().desc(), art.id.desc()));
            case BID_COUNT_ASC -> orderBy.addAll(List.of(auctionRecord.count().asc(), art.id.desc()));
            default -> orderBy.addAll(List.of(auctionRecord.count().desc(), art.id.desc()));
        }

        return orderBy;
    }

    private List<AuctionArt> completeAuctionArts(
            final JPAQuery<AuctionArt> fetchQuery,
            final SearchSortType sortType
    ) {
        final List<AuctionArt> result = addAuctionArtJoinBySortOption(fetchQuery, sortType);

        if (!result.isEmpty()) {
            applyAuctionArtMetaData(result);
        }

        return result;
    }

    private List<GeneralArt> completeGeneralArts(
            final JPAQuery<GeneralArt> fetchQuery,
            final SearchSortType sortType
    ) {
        final List<GeneralArt> result = addGeneralArtJoinBySortOption(fetchQuery, sortType);

        if (!result.isEmpty()) {
            applyGeneralArtMetaData(result);
        }

        return result;
    }

    private List<AuctionArt> addAuctionArtJoinBySortOption(
            final JPAQuery<AuctionArt> fetchQuery,
            final SearchSortType sortType
    ) {
        return switch (sortType) {
            case BID_COUNT_ASC, BID_COUNT_DESC -> fetchQuery
                    .leftJoin(auctionRecord).on(auctionRecord.auction.id.eq(auction.id))
                    .groupBy(getAuctionArtGroupByExpressions())
                    .fetch();
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(favorite).on(favorite.art.id.eq(art.id))
                    .groupBy(getAuctionArtGroupByExpressions())
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private List<GeneralArt> addGeneralArtJoinBySortOption(
            final JPAQuery<GeneralArt> fetchQuery,
            final SearchSortType sortType
    ) {
        return switch (sortType) {
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(favorite).on(favorite.art.id.eq(art.id))
                    .groupBy(getGeneralArtGroupByExpressions())
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private Expression<?> getAuctionArtGroupByExpressions() {
        return Expressions.list(
                auction.id,
                auction.highestBid.bidPrice,
                auction.period.startDate,
                auction.period.endDate,
                art.id,
                art.name,
                art.description,
                art.price,
                art.status,
                art.uploadImage,
                art.createdAt,
                owner.id,
                owner.nickname,
                owner.school,
                highestBidder.id,
                highestBidder.nickname,
                highestBidder.school
        );
    }

    private Expression<?> getGeneralArtGroupByExpressions() {
        return Expressions.list(
                art.id,
                art.name,
                art.description,
                art.price,
                art.status,
                art.uploadImage,
                art.createdAt,
                owner.id,
                owner.nickname,
                owner.school,
                buyer.id,
                buyer.nickname,
                buyer.school
        );
    }

    private void applyAuctionArtMetaData(final List<AuctionArt> result) {
        final List<Long> artIds = result.stream()
                .map(AuctionArt::getArtId)
                .toList();
        final List<HashtagSummary> hashtagSummaries = getHashtagSummaries(artIds);
        final List<FavoriteSummary> favoriteSummaries = getFavoriteSummaries(artIds);
        final List<AuctionActivitySummary> auctionActivitySummaries = getAuctionActivitySummaries(artIds);

        result.forEach(art -> {
            art.applyHashtags(collectHashtags(hashtagSummaries, art.getArtId()));
            art.applyLikeMembers(collectLikeMembers(favoriteSummaries, art.getArtId()));
            art.applyBidCount(collectBidCount(auctionActivitySummaries, art.getArtId()));
        });
    }

    private void applyGeneralArtMetaData(final List<GeneralArt> result) {
        final List<Long> artIds = result.stream()
                .map(GeneralArt::getArtId)
                .toList();
        final List<HashtagSummary> hashtagSummaries = getHashtagSummaries(artIds);
        final List<FavoriteSummary> favoriteSummaries = getFavoriteSummaries(artIds);

        result.forEach(art -> {
            art.applyHashtags(collectHashtags(hashtagSummaries, art.getArtId()));
            art.applyLikeMembers(collectLikeMembers(favoriteSummaries, art.getArtId()));
        });
    }

    private List<HashtagSummary> getHashtagSummaries(final List<Long> artIds) {
        return query
                .select(new QHashtagSummary(
                        hashtag.art.id,
                        hashtag.name
                ))
                .from(hashtag)
                .where(hashtag.art.id.in(artIds))
                .fetch();
    }

    private List<String> collectHashtags(
            final List<HashtagSummary> hashtagSummaries,
            final Long artId
    ) {
        return hashtagSummaries.stream()
                .filter(hashtag -> hashtag.artId().equals(artId))
                .map(HashtagSummary::name)
                .toList();
    }

    private List<FavoriteSummary> getFavoriteSummaries(final List<Long> artIds) {
        return query
                .select(new QFavoriteSummary(
                        favorite.art.id,
                        favorite.member.id
                ))
                .from(favorite)
                .where(favorite.art.id.in(artIds))
                .fetch();
    }

    private List<Long> collectLikeMembers(
            final List<FavoriteSummary> favoriteSummaries,
            final Long artId
    ) {
        return favoriteSummaries.stream()
                .filter(favorite -> favorite.artId().equals(artId))
                .map(FavoriteSummary::memberId)
                .toList();
    }

    private List<AuctionActivitySummary> getAuctionActivitySummaries(final List<Long> artIds) {
        return query
                .select(new QAuctionActivitySummary(
                        auction.art.id,
                        auction.count()
                ))
                .from(auction)
                .innerJoin(auctionRecord).on(auctionRecord.auction.id.eq(auction.id))
                .where(auction.art.id.in(artIds))
                .groupBy(auction.id)
                .fetch();
    }

    private int collectBidCount(
            final List<AuctionActivitySummary> auctionActivitySummaries,
            final Long artId
    ) {
        return auctionActivitySummaries.stream()
                .filter(auction -> auction.artId().equals(artId))
                .map(AuctionActivitySummary::bidCount)
                .findFirst()
                .orElse(0L)
                .intValue();
    }

    private BooleanExpression auctionIsInProgress() {
        final LocalDateTime now = LocalDateTime.now();

        return auction.period.startDate.before(now)
                .and(auction.period.endDate.after(now));
    }

    private BooleanExpression artKeywordEq(final String givenKeyword) {
        if (givenKeyword == null) {
            return null;
        }

        return art.name.value.contains(givenKeyword).or(art.description.value.contains(givenKeyword));
    }

    private BooleanExpression artHashtagEq(final String givenHashtag) {
        if (givenHashtag == null) {
            return null;
        }

        return hashtag.name.eq(givenHashtag);
    }

    private BooleanExpression artIdIn(final List<Long> artIds) {
        if (CollectionUtils.isEmpty(artIds)) {
            return null;
        }

        return art.id.in(artIds);
    }
}

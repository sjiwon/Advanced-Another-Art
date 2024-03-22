package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionActivitySummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.HashtagSummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.LikeSummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.QAuctionActivitySummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.QAuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.QGeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.QHashtagSummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.QLikeSummary;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.SortType;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.member.domain.model.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.Art.Type.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.Art.Type.GENERAL;
import static com.sjiwon.anotherart.art.domain.model.QArt.art;
import static com.sjiwon.anotherart.art.domain.model.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.model.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.model.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.like.domain.model.QLike.like;
import static com.sjiwon.anotherart.member.domain.model.QMember.member;
import static com.sjiwon.anotherart.purchase.domain.model.QPurchase.purchase;

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
                condition.sortType(),
                pageable,
                Arrays.asList(
                        artTypeEq(AUCTION),
                        auctionIsInProgress()
                )
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .innerJoin(auction).on(auction.artId.eq(art.id))
                .where(
                        artTypeEq(AUCTION),
                        auctionIsInProgress()
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<AuctionArt> fetchAuctionArtsByKeyword(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<AuctionArt> result = projectionAuctionArts(
                condition.sortType(),
                pageable,
                Arrays.asList(
                        artTypeEq(AUCTION),
                        artKeywordEq(condition.value())
                )
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .innerJoin(auction).on(auction.artId.eq(art.id))
                .where(
                        artTypeEq(AUCTION),
                        artKeywordEq(condition.value())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<GeneralArt> fetchGeneralArtsByKeyword(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<GeneralArt> result = projectionGeneralArts(
                condition.sortType(),
                pageable,
                Arrays.asList(
                        artTypeEq(GENERAL),
                        artKeywordEq(condition.value())
                )
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .where(
                        artTypeEq(GENERAL),
                        artKeywordEq(condition.value())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<AuctionArt> fetchAuctionArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<Long> artIds = query
                .select(art.id)
                .from(art)
                .innerJoin(hashtag).on(hashtag.art.id.eq(art.id))
                .where(
                        artTypeEq(AUCTION),
                        artHashtagEq(condition.value())
                )
                .fetch();
        final List<AuctionArt> result = projectionAuctionArts(
                condition.sortType(),
                pageable,
                Arrays.asList(artIdIn(artIds))
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .innerJoin(auction).on(auction.artId.eq(art.id))
                .where(artIdIn(artIds))
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<GeneralArt> fetchGeneralArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<Long> artIds = query
                .select(art.id)
                .from(art)
                .innerJoin(hashtag).on(hashtag.art.id.eq(art.id))
                .where(
                        artTypeEq(GENERAL),
                        artHashtagEq(condition.value())
                )
                .fetch();
        final List<GeneralArt> result = projectionGeneralArts(
                condition.sortType(),
                pageable,
                Arrays.asList(artIdIn(artIds))
        );
        final Long totalCount = query
                .select(art.id.count())
                .from(art)
                .where(artIdIn(artIds))
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    private List<AuctionArt> projectionAuctionArts(
            final SortType sortType,
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final JPAQuery<AuctionArt> fetchQuery = query
                .select(fetchAuctionArt())
                .from(art)
                .innerJoin(member, owner).on(owner.id.eq(art.ownerId))
                .innerJoin(auction).on(auction.artId.eq(art.id))
                .leftJoin(member, highestBidder).on(highestBidder.id.eq(auction.highestBidderId))
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(orderBySearchCondition(sortType, AUCTION).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return completeAuctionArts(fetchQuery, sortType);
    }

    private List<GeneralArt> projectionGeneralArts(
            final SortType sortType,
            final Pageable pageable,
            final List<BooleanExpression> whereConditions
    ) {
        final JPAQuery<GeneralArt> fetchQuery = query
                .select(fetchGeneralArt())
                .from(art)
                .innerJoin(member, owner).on(owner.id.eq(art.ownerId))
                .leftJoin(purchase).on(purchase.artId.eq(art.id))
                .leftJoin(member, buyer).on(buyer.id.eq(purchase.buyerId))
                .where(whereConditions.toArray(Predicate[]::new))
                .orderBy(orderBySearchCondition(sortType, GENERAL).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        return completeGeneralArts(fetchQuery, sortType);
    }

    private ConstructorExpression<AuctionArt> fetchAuctionArt() {
        return new QAuctionArt(
                auction.id,
                auction.highestBidPrice,
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

    private List<OrderSpecifier<?>> orderBySearchCondition(final SortType sortType, final Art.Type artType) {
        final List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sortType) {
            case DATE_ASC -> orderBy.add(art.id.asc());
            case DATE_DESC -> orderBy.add(art.id.desc());
            case PRICE_ASC -> orderBy.addAll(List.of(
                    artType == AUCTION ? auction.highestBidPrice.asc() : art.price.asc(),
                    art.id.desc()
            ));
            case PRICE_DESC -> orderBy.addAll(List.of(
                    artType == AUCTION ? auction.highestBidPrice.desc() : art.price.desc(),
                    art.id.desc()
            ));
            case LIKE_ASC -> orderBy.addAll(List.of(like.count().asc(), art.id.desc()));
            case LIKE_DESC -> orderBy.addAll(List.of(like.count().desc(), art.id.desc()));
            case BID_COUNT_ASC -> orderBy.addAll(List.of(auctionRecord.count().asc(), art.id.desc()));
            default -> orderBy.addAll(List.of(auctionRecord.count().desc(), art.id.desc()));
        }

        return orderBy;
    }

    private List<AuctionArt> completeAuctionArts(
            final JPAQuery<AuctionArt> fetchQuery,
            final SortType sortType
    ) {
        final List<AuctionArt> result = addAuctionArtJoinBySortOption(fetchQuery, sortType);

        if (!result.isEmpty()) {
            applyAuctionArtMetaData(result);
        }

        return result;
    }

    private List<GeneralArt> completeGeneralArts(
            final JPAQuery<GeneralArt> fetchQuery,
            final SortType sortType
    ) {
        final List<GeneralArt> result = addGeneralArtJoinBySortOption(fetchQuery, sortType);

        if (!result.isEmpty()) {
            applyGeneralArtMetaData(result);
        }

        return result;
    }

    private List<AuctionArt> addAuctionArtJoinBySortOption(
            final JPAQuery<AuctionArt> fetchQuery,
            final SortType sortType
    ) {
        return switch (sortType) {
            case BID_COUNT_ASC, BID_COUNT_DESC -> fetchQuery
                    .leftJoin(auctionRecord).on(auctionRecord.auction.id.eq(auction.id))
                    .groupBy(getAuctionArtGroupByExpressions())
                    .fetch();
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(like).on(like.artId.eq(art.id))
                    .groupBy(getAuctionArtGroupByExpressions())
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private List<GeneralArt> addGeneralArtJoinBySortOption(
            final JPAQuery<GeneralArt> fetchQuery,
            final SortType sortType
    ) {
        return switch (sortType) {
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(like).on(like.artId.eq(art.id))
                    .groupBy(getGeneralArtGroupByExpressions())
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private Expression<?> getAuctionArtGroupByExpressions() {
        return Expressions.list(
                auction.id,
                auction.highestBidPrice,
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
        final List<LikeSummary> favoriteSummaries = getFavoriteSummaries(artIds);
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
        final List<LikeSummary> favoriteSummaries = getFavoriteSummaries(artIds);

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
                .filter(it -> it.artId().equals(artId))
                .map(HashtagSummary::name)
                .toList();
    }

    private List<LikeSummary> getFavoriteSummaries(final List<Long> artIds) {
        return query
                .select(new QLikeSummary(
                        like.artId,
                        like.memberId
                ))
                .from(like)
                .where(like.artId.in(artIds))
                .fetch();
    }

    private List<Long> collectLikeMembers(
            final List<LikeSummary> favoriteSummaries,
            final Long artId
    ) {
        return favoriteSummaries.stream()
                .filter(it -> it.artId().equals(artId))
                .map(LikeSummary::memberId)
                .toList();
    }

    private List<AuctionActivitySummary> getAuctionActivitySummaries(final List<Long> artIds) {
        return query
                .select(new QAuctionActivitySummary(
                        auction.artId,
                        auction.count()
                ))
                .from(auction)
                .innerJoin(auctionRecord).on(auctionRecord.auction.id.eq(auction.id))
                .where(auction.artId.in(artIds))
                .groupBy(auction.id)
                .fetch();
    }

    private int collectBidCount(
            final List<AuctionActivitySummary> auctionActivitySummaries,
            final Long artId
    ) {
        return auctionActivitySummaries.stream()
                .filter(it -> it.artId().equals(artId))
                .map(AuctionActivitySummary::bidCount)
                .findFirst()
                .orElse(0L)
                .intValue();
    }

    private BooleanExpression artTypeEq(final Art.Type type) {
        return art.type.eq(type);
    }

    private BooleanExpression auctionIsInProgress() {
        final LocalDateTime now = LocalDateTime.now();

        return auction.period.startDate.before(now)
                .and(auction.period.endDate.after(now));
    }

    private BooleanExpression artKeywordEq(final String givenKeyword) {
        if (!StringUtils.hasText(givenKeyword)) {
            return null;
        }

        return art.name.value.contains(givenKeyword).or(art.description.value.contains(givenKeyword));
    }

    private BooleanExpression artHashtagEq(final String givenHashtag) {
        if (!StringUtils.hasText(givenHashtag)) {
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

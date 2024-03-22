package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionActivitySummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.HashtagSummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.LikeSummary;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.SortType;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.jooq.tables.JMember;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.GroupField;
import org.jooq.OrderField;
import org.jooq.SelectOnConditionStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.Art.Type.GENERAL;
import static com.sjiwon.anotherart.jooq.tables.JArt.ART;
import static com.sjiwon.anotherart.jooq.tables.JArtHashtag.ART_HASHTAG;
import static com.sjiwon.anotherart.jooq.tables.JArtLike.ART_LIKE;
import static com.sjiwon.anotherart.jooq.tables.JArtPurchase.ART_PURCHASE;
import static com.sjiwon.anotherart.jooq.tables.JAuction.AUCTION;
import static com.sjiwon.anotherart.jooq.tables.JAuctionRecord.AUCTION_RECORD;
import static org.jooq.impl.DSL.count;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class ArtDetailJooqRepository implements ArtDetailQueryRepository {
    private static final JMember OWNER = new JMember("owner");
    private static final JMember BUYER = new JMember("buyer");
    private static final JMember HIGHEST_BIDDER = new JMember("highestBidder");

    private final DSLContext dsl;

    @Override
    public Page<AuctionArt> fetchActiveAuctionArts(
            final ActiveAuctionArtsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<AuctionArt> result = projectionAuctionArts(
                condition.sortType(),
                pageable,
                Arrays.asList(
                        artTypeEq(Art.Type.AUCTION),
                        auctionIsInProgress()
                )
        );
        final Long totalCount = dsl
                .select(count(ART.ID))
                .from(ART)
                .innerJoin(AUCTION).on(AUCTION.ART_ID.eq(ART.ID))
                .where(
                        artTypeEq(Art.Type.AUCTION),
                        auctionIsInProgress()
                )
                .fetchOneInto(Long.class);
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
                        artTypeEq(Art.Type.AUCTION),
                        artKeywordEq(condition.value())
                )
        );
        final Long totalCount = dsl
                .select(count(ART.ID))
                .from(ART)
                .innerJoin(AUCTION).on(AUCTION.ART_ID.eq(ART.ID))
                .where(
                        artTypeEq(Art.Type.AUCTION),
                        artKeywordEq(condition.value())
                )
                .fetchOneInto(Long.class);
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
        final Long totalCount = dsl
                .select(count(ART.ID))
                .from(ART)
                .where(
                        artTypeEq(Art.Type.GENERAL),
                        artKeywordEq(condition.value())
                )
                .fetchOneInto(Long.class);
        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<AuctionArt> fetchAuctionArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<Long> artIds = dsl
                .select(ART.ID)
                .from(ART)
                .innerJoin(ART_HASHTAG).on(ART_HASHTAG.ART_ID.eq(ART.ID))
                .where(
                        artTypeEq(Art.Type.AUCTION),
                        artHashtagEq(condition.value())
                )
                .fetchInto(Long.class);
        final List<AuctionArt> result = projectionAuctionArts(
                condition.sortType(),
                pageable,
                Arrays.asList(artIdIn(artIds))
        );
        final Long totalCount = dsl
                .select(count(ART.ID))
                .from(ART)
                .innerJoin(AUCTION).on(AUCTION.ART_ID.eq(ART.ID))
                .where(artIdIn(artIds))
                .fetchOneInto(Long.class);
        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    @Override
    public Page<GeneralArt> fetchGeneralArtsByHashtag(
            final ArtDetailsSearchCondition condition,
            final Pageable pageable
    ) {
        final List<Long> artIds = dsl
                .select(ART.ID)
                .from(ART)
                .innerJoin(ART_HASHTAG).on(ART_HASHTAG.ART_ID.eq(ART.ID))
                .where(
                        artTypeEq(Art.Type.GENERAL),
                        artHashtagEq(condition.value())
                )
                .fetchInto(Long.class);
        final List<GeneralArt> result = projectionGeneralArts(
                condition.sortType(),
                pageable,
                Arrays.asList(artIdIn(artIds))
        );
        final Long totalCount = dsl
                .select(count(ART.ID))
                .from(ART)
                .where(artIdIn(artIds))
                .fetchOneInto(Long.class);
        return PageableExecutionUtils.getPage(result, pageable, () -> totalCount);
    }

    private List<AuctionArt> projectionAuctionArts(
            final SortType sortType,
            final Pageable pageable,
            final List<Condition> whereConditions
    ) {
        final SelectOnConditionStep<?> fetchQuery = dsl
                .select(
                        AUCTION.ID,
                        AUCTION.HIGHEST_BID_PRICE,
                        AUCTION.START_DATE,
                        AUCTION.END_DATE,
                        ART.ID,
                        ART.NAME,
                        ART.DESCRIPTION,
                        ART.PRICE,
                        ART.ART_STATUS,
                        ART.LINK,
                        ART.CREATED_AT,
                        OWNER.ID,
                        OWNER.NICKNAME,
                        OWNER.SCHOOL,
                        HIGHEST_BIDDER.ID,
                        HIGHEST_BIDDER.NICKNAME,
                        HIGHEST_BIDDER.SCHOOL
                )
                .from(ART)
                .innerJoin(OWNER).on(OWNER.ID.eq(ART.OWNER_ID))
                .innerJoin(AUCTION).on(AUCTION.ART_ID.eq(ART.ID))
                .leftJoin(HIGHEST_BIDDER).on(HIGHEST_BIDDER.ID.eq(AUCTION.HIGHEST_BIDDER_ID));

        final List<AuctionArt> result = completeAuctionArts(fetchQuery, sortType, pageable, whereConditions);
        if (!result.isEmpty()) {
            applyAuctionArtMetaData(result);
        }
        return result;
    }

    private List<GeneralArt> projectionGeneralArts(
            final SortType sortType,
            final Pageable pageable,
            final List<Condition> whereConditions
    ) {
        final SelectOnConditionStep<?> fetchQuery = dsl
                .select(
                        ART.ID,
                        ART.NAME,
                        ART.DESCRIPTION,
                        ART.PRICE,
                        ART.ART_STATUS,
                        ART.LINK,
                        ART.CREATED_AT,
                        OWNER.ID,
                        OWNER.NICKNAME,
                        OWNER.SCHOOL,
                        BUYER.ID,
                        BUYER.NICKNAME,
                        BUYER.SCHOOL
                )
                .from(ART)
                .innerJoin(OWNER).on(OWNER.ID.eq(ART.OWNER_ID))
                .leftJoin(ART_PURCHASE).on(ART_PURCHASE.ART_ID.eq(ART.ID))
                .leftJoin(BUYER).on(BUYER.ID.eq(ART_PURCHASE.BUYER_ID));

        final List<GeneralArt> result = completeGeneralArts(fetchQuery, sortType, pageable, whereConditions);
        if (!result.isEmpty()) {
            applyGeneralArtMetaData(result);
        }
        return result;
    }

    private List<AuctionArt> completeAuctionArts(
            final SelectOnConditionStep<?> fetchQuery,
            final SortType sortType,
            final Pageable pageable,
            final List<Condition> whereConditions
    ) {
        return switch (sortType) {
            case BID_COUNT_ASC, BID_COUNT_DESC -> fetchQuery
                    .leftJoin(AUCTION_RECORD).on(AUCTION_RECORD.AUCTION_ID.eq(AUCTION.ID))
                    .where(whereConditions.toArray(Condition[]::new))
                    .groupBy(createAuctionArtGroupBy())
                    .orderBy(orderBySearchCondition(sortType, Art.Type.AUCTION).toArray(OrderField[]::new))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchInto(AuctionArt.class);
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(ART_LIKE).on(ART_LIKE.ART_ID.eq(ART.ID))
                    .where(whereConditions.toArray(Condition[]::new))
                    .groupBy(createAuctionArtGroupBy())
                    .orderBy(orderBySearchCondition(sortType, Art.Type.AUCTION).toArray(OrderField[]::new))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchInto(AuctionArt.class);
            default -> fetchQuery
                    .where(whereConditions.toArray(Condition[]::new))
                    .orderBy(orderBySearchCondition(sortType, Art.Type.AUCTION).toArray(OrderField[]::new))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchInto(AuctionArt.class);
        };
    }

    private List<GeneralArt> completeGeneralArts(
            final SelectOnConditionStep<?> fetchQuery,
            final SortType sortType,
            final Pageable pageable,
            final List<Condition> whereConditions
    ) {
        return switch (sortType) {
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(ART_LIKE).on(ART_LIKE.ART_ID.eq(ART.ID))
                    .where(whereConditions.toArray(Condition[]::new))
                    .groupBy(createGeneralArtGroupBy())
                    .orderBy(orderBySearchCondition(sortType, Art.Type.GENERAL).toArray(OrderField[]::new))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchInto(GeneralArt.class);
            default -> fetchQuery
                    .where(whereConditions.toArray(Condition[]::new))
                    .orderBy(orderBySearchCondition(sortType, Art.Type.GENERAL).toArray(OrderField[]::new))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetchInto(GeneralArt.class);
        };
    }

    private List<GroupField> createAuctionArtGroupBy() {
        return List.of(
                AUCTION.ID,
                AUCTION.HIGHEST_BID_PRICE,
                AUCTION.START_DATE,
                AUCTION.END_DATE,
                ART.ID,
                ART.NAME,
                ART.DESCRIPTION,
                ART.PRICE,
                ART.ART_STATUS,
                ART.LINK,
                ART.CREATED_AT,
                OWNER.ID,
                OWNER.NICKNAME,
                OWNER.SCHOOL,
                HIGHEST_BIDDER.ID,
                HIGHEST_BIDDER.NICKNAME,
                HIGHEST_BIDDER.SCHOOL
        );
    }

    private List<GroupField> createGeneralArtGroupBy() {
        return List.of(
                ART.ID,
                ART.NAME,
                ART.DESCRIPTION,
                ART.PRICE,
                ART.ART_STATUS,
                ART.LINK,
                ART.CREATED_AT,
                OWNER.ID,
                OWNER.NICKNAME,
                OWNER.SCHOOL,
                BUYER.ID,
                BUYER.NICKNAME,
                BUYER.SCHOOL
        );
    }

    private List<OrderField<?>> orderBySearchCondition(final SortType sortType, final Art.Type artType) {
        return switch (sortType) {
            case DATE_ASC -> List.of(ART.ID.asc());
            case DATE_DESC -> List.of(ART.ID.desc());
            case PRICE_ASC -> List.of(
                    artType == Art.Type.AUCTION ? AUCTION.HIGHEST_BID_PRICE.asc() : ART.PRICE.asc(),
                    ART.ID.desc()
            );
            case PRICE_DESC -> List.of(
                    artType == Art.Type.AUCTION ? AUCTION.HIGHEST_BID_PRICE.desc() : ART.PRICE.desc(),
                    ART.ID.desc()
            );
            case LIKE_ASC -> List.of(count(ART_LIKE).asc(), ART.ID.desc());
            case LIKE_DESC -> List.of(count(ART_LIKE).desc(), ART.ID.desc());
            case BID_COUNT_ASC -> List.of(count(AUCTION_RECORD).asc(), ART.ID.desc());
            default -> List.of(count(AUCTION_RECORD).desc(), ART.ID.desc());
        };
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
        return dsl.select(
                        ART_HASHTAG.ART_ID,
                        ART_HASHTAG.NAME
                )
                .from(ART_HASHTAG)
                .where(ART_HASHTAG.ART_ID.in(artIds))
                .fetchInto(HashtagSummary.class);
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
        return dsl.select(
                        ART_LIKE.ART_ID,
                        ART_LIKE.MEMBER_ID
                )
                .from(ART_LIKE)
                .where(ART_LIKE.ART_ID.in(artIds))
                .fetchInto(LikeSummary.class);
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
        return dsl.select(
                        AUCTION.ART_ID,
                        count(AUCTION.ART_ID)
                )
                .from(AUCTION)
                .innerJoin(AUCTION_RECORD).on(AUCTION_RECORD.AUCTION_ID.eq(AUCTION.ID))
                .where(AUCTION.ART_ID.in(artIds))
                .groupBy(AUCTION.ID)
                .fetchInto(AuctionActivitySummary.class);
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

    private Condition artTypeEq(final Art.Type type) {
        return ART.ART_TYPE.eq(type.name());
    }

    private Condition auctionIsInProgress() {
        final LocalDateTime now = LocalDateTime.now();
        return AUCTION.START_DATE.lt(now)
                .and(AUCTION.END_DATE.gt(now));
    }

    private Condition artKeywordEq(final String givenKeyword) {
        if (!StringUtils.hasText(givenKeyword)) {
            return null;
        }
        return ART.NAME.contains(givenKeyword).or(ART.DESCRIPTION.contains(givenKeyword));
    }

    private Condition artHashtagEq(final String givenHashtag) {
        if (!StringUtils.hasText(givenHashtag)) {
            return null;
        }
        return ART_HASHTAG.NAME.eq(givenHashtag);
    }

    private Condition artIdIn(final List<Long> artIds) {
        if (CollectionUtils.isEmpty(artIds)) {
            return null;
        }
        return ART.ID.in(artIds);
    }
}

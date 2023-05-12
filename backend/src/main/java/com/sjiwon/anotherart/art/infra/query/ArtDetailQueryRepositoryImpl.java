package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.response.*;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.art.utils.search.SortType;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.domain.hashtag.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;
import static com.sjiwon.anotherart.purchase.domain.QPurchase.purchase;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtDetailQueryRepositoryImpl implements ArtDetailQueryRepository {
    private final JPAQueryFactory query;

    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");
    private static final QMember highestBidder = new QMember("highestBidder");

    @Override
    public Page<AuctionArt> findActiveAuctionArts(SortType sortType, Pageable pageable) {
        JPAQuery<AuctionArt> fetchQuery = query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.bidders.highestBidder, highestBidder)
                .where(auctionIsInProgress())
                .orderBy(orderBySearchCondition(sortType, AUCTION).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<AuctionArt> result = makeAuctionArtFetchQueryResult(fetchQuery, sortType);
        Long count = query
                .select(art.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(auctionIsInProgress())
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> count);
    }

    @Override
    public Page<AuctionArt> findAuctionArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable) {
        JPAQuery<AuctionArt> fetchQuery = query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.bidders.highestBidder, highestBidder)
                .where(
                        artTypeEq(condition.artType()),
                        artKeywordEq(condition.value())
                )
                .orderBy(orderBySearchCondition(condition.sortType(), AUCTION).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<AuctionArt> result = makeAuctionArtFetchQueryResult(fetchQuery, condition.sortType());
        Long count = query
                .select(art.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(
                        artTypeEq(condition.artType()),
                        artKeywordEq(condition.value())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> count);
    }

    @Override
    public Page<AuctionArt> findAuctionArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable) {
        List<Long> artIdsWithHashtag = query
                .selectDistinct(art.id)
                .from(art)
                .innerJoin(hashtag).on(hashtag.art.id.eq(art.id))
                .where(hashtag.name.eq(condition.value()))
                .fetch();

        JPAQuery<AuctionArt> fetchQuery = query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.bidders.highestBidder, highestBidder)
                .where(
                        artTypeEq(condition.artType()),
                        art.id.in(artIdsWithHashtag)
                )
                .orderBy(orderBySearchCondition(condition.sortType(), AUCTION).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<AuctionArt> result = makeAuctionArtFetchQueryResult(fetchQuery, condition.sortType());
        Long count = query
                .select(art.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(
                        artTypeEq(condition.artType()),
                        art.id.in(artIdsWithHashtag)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> count);
    }

    @Override
    public Page<GeneralArt> findGeneralArtsByKeyword(ArtDetailSearchCondition condition, Pageable pageable) {
        JPAQuery<GeneralArt> fetchQuery = query
                .select(assembleGeneralArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(
                        artTypeEq(condition.artType()),
                        artKeywordEq(condition.value())
                )
                .orderBy(orderBySearchCondition(condition.sortType(), GENERAL).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<GeneralArt> result = makeGeneralArtFetchQueryResult(fetchQuery, condition.sortType());
        Long count = query
                .select(art.count())
                .from(art)
                .where(
                        artTypeEq(condition.artType()),
                        artKeywordEq(condition.value())
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> count);
    }

    @Override
    public Page<GeneralArt> findGeneralArtsByHashtag(ArtDetailSearchCondition condition, Pageable pageable) {
        List<Long> artIdsWithHashtag = query
                .selectDistinct(art.id)
                .from(art)
                .innerJoin(hashtag).on(hashtag.art.id.eq(art.id))
                .where(hashtag.name.eq(condition.value()))
                .fetch();

        JPAQuery<GeneralArt> fetchQuery = query
                .select(assembleGeneralArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(
                        artTypeEq(condition.artType()),
                        art.id.in(artIdsWithHashtag)
                )
                .orderBy(orderBySearchCondition(condition.sortType(), GENERAL).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<GeneralArt> result = makeGeneralArtFetchQueryResult(fetchQuery, condition.sortType());
        Long count = query
                .select(art.count())
                .from(art)
                .where(
                        artTypeEq(condition.artType()),
                        art.id.in(artIdsWithHashtag)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(result, pageable, () -> count);
    }

    private List<AuctionArt> makeAuctionArtFetchQueryResult(JPAQuery<AuctionArt> fetchQuery, SortType sortType) {
        List<AuctionArt> result = addAuctionArtJoinBySortOption(fetchQuery, sortType);
        List<Long> artIds = result.stream()
                .map(AuctionArt::getArt)
                .map(BasicArt::getId)
                .toList();

        applyHashtagAndLikeCountAndBidCount(result, artIds);
        return result;
    }

    private List<GeneralArt> makeGeneralArtFetchQueryResult(JPAQuery<GeneralArt> fetchQuery, SortType sortType) {
        List<GeneralArt> result = addGeneralArtJoinBySortOption(fetchQuery, sortType);
        List<Long> artIds = result.stream()
                .map(GeneralArt::getArt)
                .map(BasicArt::getId)
                .toList();

        applyHashtagAndLikeCount(result, artIds);
        return result;
    }

    private List<AuctionArt> addAuctionArtJoinBySortOption(JPAQuery<AuctionArt> fetchQuery, SortType sortType) {
        return switch (sortType) {
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(favorite).on(favorite.artId.eq(art.id))
                    .groupBy(art.id)
                    .fetch();
            case BID_COUNT_ASC, BID_COUNT_DESC -> fetchQuery
                    .leftJoin(auctionRecord).on(auctionRecord.auction.id.eq(auction.id))
                    .groupBy(auction.id)
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    private List<GeneralArt> addGeneralArtJoinBySortOption(JPAQuery<GeneralArt> fetchQuery, SortType sortType) {
        return switch (sortType) {
            case LIKE_ASC, LIKE_DESC -> fetchQuery
                    .leftJoin(favorite).on(favorite.artId.eq(art.id))
                    .groupBy(art.id)
                    .fetch();
            default -> fetchQuery.fetch();
        };
    }

    public static ConstructorExpression<AuctionArt> assembleAuctionArtProjections() {
        return new QAuctionArt(
                auction.id, auction.bidders.highestBidPrice, auction.period.startDate, auction.period.endDate,
                art.id, art.name, art.description, art.price, art.status, art.storageName, art.createdAt,
                owner.id, owner.nickname, owner.school,
                highestBidder.id, highestBidder.nickname, highestBidder.school
        );
    }

    public static ConstructorExpression<GeneralArt> assembleGeneralArtProjections() {
        return new QGeneralArt(
                art.id, art.name, art.description, art.price, art.status, art.storageName, art.createdAt,
                owner.id, owner.nickname, owner.school,
                buyer.id, buyer.nickname, buyer.school
        );
    }

    private List<OrderSpecifier<?>> orderBySearchCondition(SortType sortType, ArtType artType) {
        List<OrderSpecifier<?>> orderBy = new LinkedList<>();

        switch (sortType) {
            case DATE_ASC -> orderBy.add(art.id.asc());
            case DATE_DESC -> orderBy.add(art.id.desc());
            case PRICE_ASC -> orderBy.add(artType == AUCTION ? auction.bidders.highestBidPrice.asc() : art.price.asc());
            case PRICE_DESC -> orderBy.add(artType == AUCTION ? auction.bidders.highestBidPrice.desc() : art.price.desc());
            case LIKE_ASC -> orderBy.add(favorite.count().asc());
            case LIKE_DESC -> orderBy.add(favorite.count().desc());
            case BID_COUNT_ASC -> orderBy.add(auctionRecord.count().asc());
            default -> orderBy.add(auctionRecord.count().desc());
        }

        return orderBy;
    }

    private BooleanExpression auctionIsInProgress() {
        final LocalDateTime now = LocalDateTime.now();

        return auction.period.startDate.before(now)
                .and(auction.period.endDate.after(now));
    }

    private BooleanExpression artTypeEq(ArtType type) {
        return (type != null) ? art.type.eq(type) : null;
    }
    
    private BooleanExpression artKeywordEq(String keyword) {
        return (keyword != null) ? art.name.value.contains(keyword).or(art.description.value.contains(keyword)) : null;
    }

    private void applyHashtagAndLikeCountAndBidCount(List<AuctionArt> result, List<Long> artIds) {
        List<SimpleHashtag> hashtags = getHashtags(artIds);
        result.forEach(args -> args.applyHashtags(collectHashtags(hashtags, args.getArt().getId())));

        List<Favorite> favorites = getFavorites(artIds);
        result.forEach(args -> args.applyLikeMembers(collectLikeMemberIds(favorites, args.getArt().getId())));

        List<SimpleAuction> auctions = getAuctions(artIds);
        result.forEach(args -> args.applyBidCount(getBidCount(auctions, args.getArt().getId())));
    }

    private void applyHashtagAndLikeCount(List<GeneralArt> result, List<Long> artIds) {
        List<SimpleHashtag> hashtags = getHashtags(artIds);
        result.forEach(args -> args.applyHashtags(collectHashtags(hashtags, args.getArt().getId())));

        List<Favorite> favorites = getFavorites(artIds);
        result.forEach(args -> args.applyLikeMembers(collectLikeMemberIds(favorites, args.getArt().getId())));
    }

    private List<SimpleHashtag> getHashtags(List<Long> artIds) {
        return query
                .select(new QSimpleHashtag(art.id, hashtag.name))
                .from(hashtag)
                .innerJoin(art).on(art.id.eq(hashtag.art.id))
                .where(art.id.in(artIds))
                .fetch();
    }

    private List<String> collectHashtags(List<SimpleHashtag> hashtags, Long artId) {
        return hashtags
                .stream()
                .filter(hashtag -> hashtag.artId().equals(artId))
                .map(SimpleHashtag::name)
                .toList();
    }

    private List<Favorite> getFavorites(List<Long> artIds) {
        return query
                .selectFrom(favorite)
                .where(favorite.artId.in(artIds))
                .fetch();
    }

    private List<Long> collectLikeMemberIds(List<Favorite> favorites, Long artId) {
        return favorites
                .stream()
                .filter(favorite -> favorite.getArtId().equals(artId))
                .map(Favorite::getMemberId)
                .toList();
    }

    private List<SimpleAuction> getAuctions(List<Long> artIds) {
        return query
                .select(new QSimpleAuction(art.id, auctionRecord.count().intValue()))
                .from(auctionRecord)
                .innerJoin(auctionRecord.auction, auction)
                .innerJoin(auction.art, art)
                .where(art.id.in(artIds))
                .groupBy(art.id)
                .fetch();
    }

    private int getBidCount(List<SimpleAuction> auctions, Long artId) {
        return auctions
                .stream()
                .filter(auction -> auction.artId().equals(artId))
                .map(SimpleAuction::bidCount)
                .findFirst()
                .orElse(0);
    }
}

package com.sjiwon.anotherart.member.infra.query;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.response.*;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.member.domain.QMember;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.QMemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.QTradedArt;
import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.domain.hashtag.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;
import static com.sjiwon.anotherart.member.domain.point.QPointRecord.pointRecord;
import static com.sjiwon.anotherart.purchase.domain.QPurchase.purchase;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationQueryRepositoryImpl implements MemberInformationQueryRepository {
    private final JPAQueryFactory query;

    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");
    private static final QMember highestBidder = new QMember("highestBidder");

    @Override
    public List<MemberPointRecord> findPointRecordByMemberId(Long memberId) {
        return query
                .select(new QMemberPointRecord(pointRecord.type, pointRecord.amount, pointRecord.createdAt))
                .from(pointRecord)
                .where(pointRecord.member.id.eq(memberId))
                .orderBy(pointRecord.id.desc())
                .fetch();
    }

    @Override
    public List<AuctionArt> findWinningAuctionArtByMemberId(Long memberId) {
        List<AuctionArt> result = query
                .select(assembleAuctionArtProjections())
                .from(auction)
                .innerJoin(auction.art, art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction.bidders.highestBidder, highestBidder)
                .where(
                        highestBidder.id.eq(memberId),
                        auctionIsFinished(),
                        artIsOnSale()
                )
                .orderBy(auction.id.desc())
                .fetch();

        List<Long> artIds = result.stream()
                .map(AuctionArt::getArt)
                .map(BasicArt::getId)
                .toList();
        applyHashtagAndLikeCountAndBidCount(result, artIds);

        return result;
    }

    @Override
    public List<TradedArt> findSoldArtByMemberIdAndType(Long memberId, ArtType type) {
        List<TradedArt> result = query
                .select(assembleTradedArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(purchase).on(purchase.art.id.eq(art.id))
                .innerJoin(purchase.buyer, buyer)
                .where(
                        artOwnerIdEq(memberId),
                        artTypeEq(type)
                )
                .orderBy(art.id.desc())
                .fetch();

        List<Long> artIds = result.stream()
                .map(TradedArt::getArt)
                .map(BasicArt::getId)
                .toList();
        applyHashtagAndLikeCount(result, artIds);

        return result;
    }

    @Override
    public List<TradedArt> findPurchaseArtByMemberIdAndType(Long memberId, ArtType type) {
        List<TradedArt> result = query
                .select(assembleTradedArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(purchase).on(purchase.art.id.eq(art.id))
                .innerJoin(purchase.buyer, buyer)
                .where(
                        artBuyerIdEq(memberId),
                        artTypeEq(type)
                )
                .orderBy(art.id.desc())
                .fetch();

        List<Long> artIds = result.stream()
                .map(TradedArt::getArt)
                .map(BasicArt::getId)
                .toList();
        applyHashtagAndLikeCount(result, artIds);

        return result;
    }

    public static ConstructorExpression<AuctionArt> assembleAuctionArtProjections() {
        return new QAuctionArt(
                auction.id, auction.bidders.highestBidPrice, auction.period.startDate, auction.period.endDate,
                art.id, art.name, art.description, art.price, art.status, art.storageName, art.createdAt,
                owner.id, owner.nickname, owner.school,
                highestBidder.id, highestBidder.nickname, highestBidder.school
        );
    }

    public static ConstructorExpression<TradedArt> assembleTradedArtProjections() {
        return new QTradedArt(
                art.id, art.name, art.description, purchase.price, art.status, art.storageName, art.createdAt,
                owner.id, owner.nickname, owner.school,
                buyer.id, buyer.nickname, buyer.school
        );
    }

    private BooleanExpression auctionIsFinished() {
        return auction.period.endDate.before(LocalDateTime.now());
    }

    private BooleanExpression artIsOnSale() {
        return art.status.eq(ON_SALE);
    }

    private BooleanExpression artOwnerIdEq(Long memberId) {
        return (memberId != null) ? owner.id.eq(memberId) : null;
    }

    private BooleanExpression artBuyerIdEq(Long memberId) {
        return (memberId != null) ? buyer.id.eq(memberId) : null;
    }

    private BooleanExpression artTypeEq(ArtType type) {
        return (type != null) ? art.type.eq(type) : null;
    }

    private void applyHashtagAndLikeCountAndBidCount(List<AuctionArt> result, List<Long> artIds) {
        List<SimpleHashtag> hashtags = getHashtags(artIds);
        result.forEach(args -> args.applyHashtags(collectHashtags(hashtags, args.getArt().getId())));

        List<Favorite> favorites = getFavorites(artIds);
        result.forEach(args -> args.applyLikeCount(getLikeCount(favorites, args.getArt().getId())));

        List<SimpleAuction> auctions = getAuctions(artIds);
        result.forEach(args -> args.applyBidCount(getBidCount(auctions, args.getArt().getId())));
    }

    private void applyHashtagAndLikeCount(List<TradedArt> result, List<Long> artIds) {
        List<SimpleHashtag> hashtags = getHashtags(artIds);
        result.forEach(args -> args.applyHashtags(collectHashtags(hashtags, args.getArt().getId())));

        List<Favorite> favorites = getFavorites(artIds);
        result.forEach(args -> args.applyLikeCount(getLikeCount(favorites, args.getArt().getId())));
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

    private int getLikeCount(List<Favorite> favorites, Long artId) {
        return (int) favorites
                .stream()
                .filter(favorite -> favorite.getArtId().equals(artId))
                .count();
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

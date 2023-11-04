package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.QAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.QGeneralArt;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
public class ArtSingleQueryRepositoryImpl implements ArtSingleQueryRepository {
    private final JPAQueryFactory query;

    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");
    private static final QMember highestBidder = new QMember("highestBidder");

    @Override
    public AuctionArt getAuctionArt(final Long artId) {
        final AuctionArt result = query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.bidders.highestBidder, highestBidder)
                .where(
                        artIdEq(artId),
                        artTypeEq(AUCTION)
                )
                .fetchOne();

        if (result != null) {
            applyHashtagAndLikeCountAndBidcount(result, artId);
        }

        return result;
    }

    @Override
    public GeneralArt getGeneralArt(final Long artId) {
        final GeneralArt result = query
                .select(assembleGeneralArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(
                        artIdEq(artId),
                        artTypeEq(GENERAL)
                )
                .fetchOne();

        if (result != null) {
            applyHashtagAndLikeCount(result, artId);
        }

        return result;
    }

    private void applyHashtagAndLikeCountAndBidcount(final AuctionArt result, final Long artId) {
        final List<String> hashtags = getHashtags(artId);
        if (!CollectionUtils.isEmpty(hashtags)) {
            result.applyHashtags(hashtags);
        }
        result.applyLikeMembers(collectLikeMemberIds(artId));
        result.applyBidCount(getBidCount(artId));
    }

    private void applyHashtagAndLikeCount(final GeneralArt result, final Long artId) {
        final List<String> hashtags = getHashtags(artId);
        if (!CollectionUtils.isEmpty(hashtags)) {
            result.applyHashtags(hashtags);
        }
        result.applyLikeMembers(collectLikeMemberIds(artId));
    }

    private List<String> getHashtags(final Long artId) {
        return query
                .select(hashtag.name)
                .from(hashtag)
                .where(hashtag.art.id.eq(artId))
                .fetch();
    }

    private List<Long> collectLikeMemberIds(final Long artId) {
        return query
                .select(favorite.memberId)
                .from(favorite)
                .where(favorite.artId.eq(artId))
                .fetch();
    }

    private int getBidCount(final Long artId) {
        return query
                .select(auctionRecord.count().intValue())
                .from(auctionRecord)
                .innerJoin(auctionRecord.auction, auction)
                .innerJoin(auction.art, art)
                .where(art.id.eq(artId))
                .fetchOne();
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

    private BooleanExpression artIdEq(final Long artId) {
        return (artId != null) ? art.id.eq(artId) : null;
    }

    private BooleanExpression artTypeEq(final ArtType type) {
        return (type != null) ? art.type.eq(type) : null;
    }
}

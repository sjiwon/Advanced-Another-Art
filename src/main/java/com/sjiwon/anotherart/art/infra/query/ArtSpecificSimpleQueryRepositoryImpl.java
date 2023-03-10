package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.domain.hashtag.QHashtag.hashtag;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.assembleAuctionArtProjections;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.assembleGeneralArtProjections;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;
import static com.sjiwon.anotherart.purchase.domain.QPurchase.purchase;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtSpecificSimpleQueryRepositoryImpl implements ArtSpecificSimpleQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");
    private static final QMember highestBidder = new QMember("highestBidder");

    @Override
    public List<String> getHashtagsById(Long artId) {
        return query
                .select(hashtag.name)
                .from(hashtag)
                .where(hashtagArtIdEq(artId))
                .fetch();
    }

    @Override
    public List<Long> getLikeMarkingMembersById(Long artId) {
        return query
                .select(favorite.memberId)
                .from(favorite)
                .where(favoriteArtIdEq(artId))
                .fetch();
    }

    @Override
    public BasicGeneralArt getGeneralArtById(Long artId) {
        return query
                .select(assembleGeneralArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(
                        artTypeEq(GENERAL),
                        artIdEq(artId)
                )
                .fetchOne();
    }

    @Override
    public BasicAuctionArt getAuctionArtById(Long artId) {
        return query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.bidder, highestBidder)
                .where(
                        artTypeEq(AUCTION),
                        artIdEq(artId)
                )
                .fetchOne();
    }

    private BooleanExpression hashtagArtIdEq(Long artId) {
        return (artId != null) ? hashtag.art.id.eq(artId) : null;
    }

    private BooleanExpression favoriteArtIdEq(Long artId) {
        return (artId != null) ? favorite.artId.eq(artId) : null;
    }

    private BooleanExpression artIdEq(Long artId) {
        return (artId != null) ? art.id.eq(artId) : null;
    }

    private BooleanExpression artTypeEq(ArtType saleType) {
        return (saleType != null) ? art.artType.eq(saleType) : null;
    }
}

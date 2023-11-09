package com.sjiwon.anotherart.art.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.GeneralArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QAuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QGeneralArt;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.member.domain.model.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.QArt.art;
import static com.sjiwon.anotherart.art.domain.model.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.model.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.model.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.model.QFavorite.favorite;
import static com.sjiwon.anotherart.purchase.domain.model.QPurchase.purchase;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class ArtSingleQueryRepositoryImpl implements ArtSingleQueryRepository {
    private final JPAQueryFactory query;

    @Override
    public ArtType getArtType(final Long artId) {
        return query
                .select(art.type)
                .from(art)
                .where(art.id.eq(artId))
                .fetchOne();
    }

    @Override
    public AuctionArt fetchAuctionArt(final Long artId) {
        final QMember owner = new QMember("owner");
        final QMember highestBidder = new QMember("highestBidder");

        final AuctionArt result = query
                .select(new QAuctionArt(
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
                ))
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.highestBid.bidder, highestBidder)
                .where(art.id.eq(artId))
                .fetchOne();

        if (result != null) {
            result.applyHashtags(getHashtags(artId));
            result.applyLikeMembers(getLikeMemberIds(artId));
            result.applyBidCount(getBidCount(artId));
        }

        return result;
    }

    @Override
    public GeneralArt fetchGeneralArt(final Long artId) {
        final QMember owner = new QMember("owner");
        final QMember buyer = new QMember("buyer");

        final GeneralArt result = query
                .select(new QGeneralArt(
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
                ))
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(art.id.eq(artId))
                .fetchOne();

        if (result != null) {
            result.applyHashtags(getHashtags(artId));
            result.applyLikeMembers(getLikeMemberIds(artId));
        }

        return result;
    }

    private List<String> getHashtags(final Long artId) {
        return query
                .select(hashtag.name)
                .from(hashtag)
                .where(hashtag.art.id.eq(artId))
                .fetch();
    }

    private List<Long> getLikeMemberIds(final Long artId) {
        return query
                .select(favorite.member.id)
                .from(favorite)
                .where(favorite.art.id.eq(artId))
                .fetch();
    }

    private int getBidCount(final Long artId) {
        return query
                .select(auctionRecord.count())
                .from(auctionRecord)
                .innerJoin(auctionRecord.auction, auction)
                .where(auction.art.id.eq(artId))
                .fetchOne()
                .intValue();
    }
}

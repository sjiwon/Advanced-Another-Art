package com.sjiwon.anotherart.art.domain.repository.query;

import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.art.domain.repository.query.response.GeneralArt;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.jooq.tables.JMember;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

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
public class ArtBasicJooqRepository implements ArtBasicQueryRepository {
    private static final JMember OWNER = new JMember("owner");
    private static final JMember HIGHEST_BIDDER = new JMember("highestBidder");
    private static final JMember BUYER = new JMember("buyer");

    private final DSLContext dsl;

    @Override
    public String getArtType(final Long artId) {
        return dsl.select(ART.ART_TYPE)
                .from(ART)
                .where(ART.ID.eq(artId))
                .fetchOneInto(String.class);
    }

    @Override
    public AuctionArt fetchAuctionArt(final Long artId) {
        final AuctionArt result = dsl.select(
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
                .leftJoin(HIGHEST_BIDDER).on(HIGHEST_BIDDER.ID.eq(AUCTION.HIGHEST_BIDDER_ID))
                .where(ART.ID.eq(artId))
                .fetchOneInto(AuctionArt.class);

        if (result != null) {
            result.applyHashtags(getHashtags(artId));
            result.applyLikeMembers(getLikeMemberIds(artId));
            result.applyBidCount(getBidCount(artId));
        }

        return result;
    }

    @Override
    public GeneralArt fetchGeneralArt(final Long artId) {
        final GeneralArt result = dsl.select(
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
                .leftJoin(BUYER).on(BUYER.ID.eq(ART_PURCHASE.BUYER_ID))
                .where(ART.ID.eq(artId))
                .fetchOneInto(GeneralArt.class);

        if (result != null) {
            result.applyHashtags(getHashtags(artId));
            result.applyLikeMembers(getLikeMemberIds(artId));
        }

        return result;
    }

    private List<String> getHashtags(final Long artId) {
        return dsl.select(ART_HASHTAG.NAME)
                .from(ART_HASHTAG)
                .where(ART_HASHTAG.ART_ID.eq(artId))
                .fetchInto(String.class);
    }

    private List<Long> getLikeMemberIds(final Long artId) {
        return dsl.select(ART_LIKE.MEMBER_ID)
                .from(ART_LIKE)
                .where(ART_LIKE.ART_ID.eq(artId))
                .fetchInto(Long.class);
    }

    private int getBidCount(final Long artId) {
        return dsl.select(count(AUCTION_RECORD))
                .from(AUCTION_RECORD)
                .innerJoin(AUCTION).on(AUCTION.ID.eq(AUCTION_RECORD.AUCTION_ID))
                .where(AUCTION.ART_ID.eq(artId))
                .fetchOneInto(Integer.class);
    }
}

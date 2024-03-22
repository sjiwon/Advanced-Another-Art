package com.sjiwon.anotherart.member.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.response.HashtagSummary;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.jooq.tables.JMember;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.WinningAuctionArt;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.jooq.tables.JArt.ART;
import static com.sjiwon.anotherart.jooq.tables.JArtHashtag.ART_HASHTAG;
import static com.sjiwon.anotherart.jooq.tables.JArtPurchase.ART_PURCHASE;
import static com.sjiwon.anotherart.jooq.tables.JAuction.AUCTION;
import static com.sjiwon.anotherart.jooq.tables.JMember.MEMBER;
import static com.sjiwon.anotherart.jooq.tables.JMemberPointRecord.MEMBER_POINT_RECORD;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class MemberInformationJooqRepository implements MemberInformationQueryRepository {
    private static final JMember OWNER = new JMember("owner");
    private static final JMember BUYER = new JMember("buyer");

    private final DSLContext dsl;

    @Override
    public MemberInformation fetchInformation(final long memberId) {
        return dsl.select(
                        MEMBER.ID,
                        MEMBER.NAME,
                        MEMBER.NICKNAME,
                        MEMBER.LOGIN_ID,
                        MEMBER.SCHOOL,
                        MEMBER.PHONE,
                        MEMBER.EMAIL,
                        MEMBER.POSTCODE,
                        MEMBER.DEFAULT_ADDRESS,
                        MEMBER.DETAIL_ADDRESS,
                        MEMBER.TOTAL_POINT,
                        MEMBER.AVAILABLE_POINT
                )
                .from(MEMBER)
                .where(MEMBER.ID.eq(memberId))
                .fetchOneInto(MemberInformation.class);
    }

    @Override
    public List<MemberPointRecord> fetchPointRecords(final long memberId) {
        return dsl.select(
                        MEMBER_POINT_RECORD.POINT_TYPE,
                        MEMBER_POINT_RECORD.AMOUNT,
                        MEMBER_POINT_RECORD.CREATED_AT
                )
                .from(MEMBER_POINT_RECORD)
                .where(MEMBER_POINT_RECORD.MEMBER_ID.eq(memberId))
                .orderBy(MEMBER_POINT_RECORD.ID.desc())
                .fetchInto(MemberPointRecord.class);
    }

    @Override
    public List<WinningAuctionArt> fetchWinningAuctionArts(final long memberId) {
        final LocalDateTime now = LocalDateTime.now();

        final List<WinningAuctionArt> result = dsl.select(
                        ART.ID,
                        ART.NAME,
                        ART.DESCRIPTION,
                        ART.LINK,
                        OWNER.NICKNAME,
                        OWNER.SCHOOL,
                        AUCTION.HIGHEST_BID_PRICE
                )
                .from(AUCTION)
                .innerJoin(ART).on(ART.ID.eq(AUCTION.ART_ID))
                .innerJoin(OWNER).on(OWNER.ID.eq(ART.OWNER_ID))
                .where(
                        AUCTION.HIGHEST_BIDDER_ID.eq(memberId),
                        AUCTION.END_DATE.lt(now)
                )
                .orderBy(AUCTION.ID.desc())
                .fetchInto(WinningAuctionArt.class);

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(WinningAuctionArt::getArtId)
                    .toList();
            final List<HashtagSummary> hashtagSummaries = getHashtags(artIds);

            result.forEach(it -> {
                final List<String> hashtags = hashtagSummaries.stream()
                        .filter(hashtag -> hashtag.artId().equals(it.getArtId()))
                        .map(HashtagSummary::name)
                        .toList();
                it.applyHashtags(hashtags);
            });
        }

        return result;
    }

    @Override
    public List<SoldArt> fetchSoldArtsByType(final long memberId, final Art.Type type) {
        final List<SoldArt> result = dsl.select(
                        ART.ID,
                        ART.NAME,
                        ART.DESCRIPTION,
                        ART.LINK,
                        BUYER.NICKNAME,
                        BUYER.SCHOOL,
                        ART_PURCHASE.PURCHASE_PRICE
                )
                .from(ART_PURCHASE)
                .innerJoin(BUYER).on(BUYER.ID.eq(ART_PURCHASE.BUYER_ID))
                .innerJoin(ART).on(ART.ID.eq(ART_PURCHASE.ART_ID))
                .where(
                        ART.OWNER_ID.eq(memberId),
                        ART.ART_TYPE.eq(type.name())
                )
                .orderBy(ART_PURCHASE.ID.desc())
                .fetchInto(SoldArt.class);

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(SoldArt::getArtId)
                    .toList();
            final List<HashtagSummary> hashtagSummaries = getHashtags(artIds);

            result.forEach(it -> {
                final List<String> hashtags = hashtagSummaries.stream()
                        .filter(hashtag -> hashtag.artId().equals(it.getArtId()))
                        .map(HashtagSummary::name)
                        .toList();
                it.applyHashtags(hashtags);
            });
        }

        return result;
    }

    @Override
    public List<PurchaseArt> fetchPurchaseArtsByType(final long memberId, final Art.Type type) {
        final List<PurchaseArt> result = dsl.select(
                        ART.ID,
                        ART.NAME,
                        ART.DESCRIPTION,
                        ART.LINK,
                        OWNER.NICKNAME,
                        OWNER.SCHOOL,
                        ART_PURCHASE.PURCHASE_PRICE
                )
                .from(ART_PURCHASE)
                .innerJoin(ART).on(ART.ID.eq(ART_PURCHASE.ART_ID))
                .innerJoin(OWNER).on(OWNER.ID.eq(ART.OWNER_ID))
                .where(
                        ART_PURCHASE.BUYER_ID.eq(memberId),
                        ART.ART_TYPE.eq(type.name())
                )
                .orderBy(ART_PURCHASE.ID.desc())
                .fetchInto(PurchaseArt.class);

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(PurchaseArt::getArtId)
                    .toList();
            final List<HashtagSummary> hashtagSummaries = getHashtags(artIds);

            result.forEach(it -> {
                final List<String> hashtags = hashtagSummaries.stream()
                        .filter(hashtag -> hashtag.artId().equals(it.getArtId()))
                        .map(HashtagSummary::name)
                        .toList();
                it.applyHashtags(hashtags);
            });
        }

        return result;
    }

    private List<HashtagSummary> getHashtags(final List<Long> artIds) {
        return dsl.select(
                        ART_HASHTAG.ART_ID,
                        ART_HASHTAG.NAME
                )
                .from(ART_HASHTAG)
                .where(ART_HASHTAG.ART_ID.in(artIds))
                .fetchInto(HashtagSummary.class);
    }
}

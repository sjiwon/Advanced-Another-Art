package com.sjiwon.anotherart.member.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.response.HashtagSummary;
import com.sjiwon.anotherart.art.domain.repository.query.response.QHashtagSummary;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.member.domain.model.QMember;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.QMemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.response.QMemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.response.QPurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.QSoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.QWinningAuctionArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.WinningAuctionArt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.QArt.art;
import static com.sjiwon.anotherart.art.domain.model.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.model.QAuction.auction;
import static com.sjiwon.anotherart.member.domain.model.QMember.member;
import static com.sjiwon.anotherart.point.domain.model.QPointRecord.pointRecord;
import static com.sjiwon.anotherart.purchase.domain.model.QPurchase.purchase;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class MemberInformationQueryRepositoryImpl implements MemberInformationQueryRepository {
    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");

    private final JPAQueryFactory query;

    @Override
    public MemberInformation fetchInformation(final long memberId) {
        return query
                .select(new QMemberInformation(
                        member.id,
                        member.name,
                        member.nickname,
                        member.loginId,
                        member.school,
                        member.phone,
                        member.email,
                        member.address,
                        member.point
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public List<MemberPointRecord> fetchPointRecords(final long memberId) {
        return query
                .select(new QMemberPointRecord(
                        pointRecord.type,
                        pointRecord.amount,
                        pointRecord.createdAt
                ))
                .from(pointRecord)
                .where(pointRecord.memberId.eq(memberId))
                .orderBy(pointRecord.id.desc())
                .fetch();
    }

    @Override
    public List<WinningAuctionArt> fetchWinningAuctionArts(final long memberId) {
        final LocalDateTime now = LocalDateTime.now();

        final List<WinningAuctionArt> result = query
                .select(new QWinningAuctionArt(
                        art.id,
                        art.name,
                        art.description,
                        art.uploadImage,
                        owner.nickname,
                        owner.school,
                        auction.highestBidPrice
                ))
                .from(auction)
                .innerJoin(art).on(art.id.eq(auction.artId))
                .innerJoin(member, owner).on(owner.id.eq(art.ownerId))
                .where(
                        auction.highestBidderId.eq(memberId),
                        auction.period.endDate.lt(now)
                )
                .orderBy(auction.id.desc())
                .fetch();

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(WinningAuctionArt::getArtId)
                    .toList();
            final List<HashtagSummary> hashtagSummaries = getHashtags(artIds);

            result.forEach(row -> {
                final List<String> hashtags = hashtagSummaries.stream()
                        .filter(hashtag -> hashtag.artId().equals(row.getArtId()))
                        .map(HashtagSummary::name)
                        .toList();
                row.applyHashtags(hashtags);
            });
        }

        return result;
    }

    @Override
    public List<SoldArt> fetchSoldArtsByType(final long memberId, final Art.Type type) {
        final List<SoldArt> result = query
                .select(new QSoldArt(
                        art.id,
                        art.name,
                        art.description,
                        art.uploadImage,
                        buyer.nickname,
                        buyer.school,
                        purchase.price
                ))
                .from(purchase)
                .innerJoin(member, buyer).on(buyer.id.eq(purchase.buyerId))
                .innerJoin(art).on(art.id.eq(purchase.artId))
                .where(
                        art.ownerId.eq(memberId),
                        art.type.eq(type)
                )
                .orderBy(purchase.id.desc())
                .fetch();

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(SoldArt::getArtId)
                    .toList();
            final List<HashtagSummary> hashtagSummaries = getHashtags(artIds);

            result.forEach(row -> {
                final List<String> hashtags = hashtagSummaries.stream()
                        .filter(hashtag -> hashtag.artId().equals(row.getArtId()))
                        .map(HashtagSummary::name)
                        .toList();
                row.applyHashtags(hashtags);
            });
        }

        return result;
    }

    @Override
    public List<PurchaseArt> fetchPurchaseArtsByType(final long memberId, final Art.Type type) {
        final List<PurchaseArt> result = query
                .select(new QPurchaseArt(
                        art.id,
                        art.name,
                        art.description,
                        art.uploadImage,
                        owner.nickname,
                        owner.school,
                        purchase.price
                ))
                .from(purchase)
                .innerJoin(art).on(art.id.eq(purchase.artId))
                .innerJoin(member, owner).on(owner.id.eq(art.ownerId))
                .where(
                        purchase.buyerId.eq(memberId),
                        art.type.eq(type)
                )
                .orderBy(purchase.id.desc())
                .fetch();

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(PurchaseArt::getArtId)
                    .toList();
            final List<HashtagSummary> hashtagSummaries = getHashtags(artIds);

            result.forEach(row -> {
                final List<String> hashtags = hashtagSummaries.stream()
                        .filter(it -> it.artId().equals(row.getArtId()))
                        .map(HashtagSummary::name)
                        .toList();
                row.applyHashtags(hashtags);
            });
        }

        return result;
    }

    private List<HashtagSummary> getHashtags(final List<Long> artIds) {
        return query
                .select(new QHashtagSummary(
                        hashtag.art.id,
                        hashtag.name
                ))
                .from(hashtag)
                .where(hashtag.art.id.in(artIds))
                .fetch();
    }
}

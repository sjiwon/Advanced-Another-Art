package com.sjiwon.anotherart.member.domain.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.repository.query.dto.QSimpleHashtag;
import com.sjiwon.anotherart.art.domain.repository.query.dto.SimpleHashtag;
import com.sjiwon.anotherart.global.annotation.AnotherArtReadOnlyTransactional;
import com.sjiwon.anotherart.member.domain.model.QMember;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.QMemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.QMemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.QWinningAuctionArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.QArt.art;
import static com.sjiwon.anotherart.art.domain.model.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.model.QAuction.auction;
import static com.sjiwon.anotherart.member.domain.model.QMember.member;
import static com.sjiwon.anotherart.point.domain.model.QPointRecord.pointRecord;

@Repository
@AnotherArtReadOnlyTransactional
@RequiredArgsConstructor
public class MemberInformationQueryRepositoryImpl implements MemberInformationQueryRepository {
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
                .where(pointRecord.member.id.eq(memberId))
                .orderBy(pointRecord.id.desc())
                .fetch();
    }

    @Override
    public List<WinningAuctionArts> fetchWinningAuctionArts(final long memberId) {
        final LocalDateTime now = LocalDateTime.now();
        final QMember bidder = new QMember("member");
        final QMember owner = new QMember("owner");

        final List<WinningAuctionArts> result = query
                .select(new QWinningAuctionArts(
                        art.id,
                        art.name,
                        art.description,
                        art.uploadImage,
                        owner.nickname,
                        owner.school,
                        auction.highestBid.bidPrice
                ))
                .from(auction)
                .innerJoin(auction.highestBid.bidder, bidder)
                .innerJoin(auction.art, art)
                .innerJoin(art.owner, owner)
                .where(
                        bidder.id.eq(memberId),
                        auction.period.endDate.lt(now)
                )
                .orderBy(auction.id.desc())
                .fetch();

        if (!result.isEmpty()) {
            final List<Long> artIds = result.stream()
                    .map(WinningAuctionArts::getArtId)
                    .toList();
            final List<SimpleHashtag> simpleHashtags = getHashtags(artIds);

            result.forEach(row -> {
                final List<String> hashtags = simpleHashtags.stream()
                        .filter(hashtag -> hashtag.artId().equals(row.getArtId()))
                        .map(SimpleHashtag::name)
                        .toList();
                row.applyHashtags(hashtags);
            });
        }

        return result;
    }

    @Override
    public List<SoldArts> fetchSoldArtsByType(final long memberId, final ArtType type) {
        return null;
    }

    @Override
    public List<PurchaseArts> fetchPurchaseArtsByType(final long memberId, final ArtType type) {
        return null;
    }

    private List<SimpleHashtag> getHashtags(final List<Long> artIds) {
        return query
                .select(new QSimpleHashtag(
                        hashtag.art.id,
                        hashtag.name
                ))
                .from(hashtag)
                .where(hashtag.art.id.in(artIds))
                .fetch();
    }
}

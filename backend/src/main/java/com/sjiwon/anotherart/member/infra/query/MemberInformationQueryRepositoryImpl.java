package com.sjiwon.anotherart.member.infra.query;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.infra.query.dto.response.*;
import com.sjiwon.anotherart.member.domain.QMember;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.QMemberPointRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.domain.hashtag.QHashtag.hashtag;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.member.domain.point.QPointRecord.pointRecord;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationQueryRepositoryImpl implements MemberInformationQueryRepository {
    private final JPAQueryFactory query;

    private static final QMember owner = new QMember("owner");
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
                        highestBidderIdEq(memberId),
                        auctionIsFinished(),
                        artIsOnSale()
                )
                .orderBy(auction.id.desc())
                .fetch();

        List<Long> artIds = result.stream()
                .map(AuctionArt::getArt)
                .map(BasicArt::getId)
                .toList();
        applyArtHashtags(result, artIds);

        return result;
    }

    private void applyArtHashtags(List<AuctionArt> result, List<Long> artIds) {
        List<BasicHashtag> hashtags = query
                .select(new QBasicHashtag(art.id, hashtag.name))
                .from(hashtag)
                .innerJoin(art).on(art.id.eq(hashtag.art.id))
                .where(art.id.in(artIds))
                .fetch();

        result.forEach(arg -> arg.applyHashtags(collectHashtags(hashtags, arg.getArt())));
    }

    private List<String> collectHashtags(List<BasicHashtag> hashtags, BasicArt art) {
        return hashtags
                .stream()
                .filter(hashtag -> hashtag.studyId().equals(art.getId()))
                .map(BasicHashtag::name)
                .toList();
    }

    public static ConstructorExpression<AuctionArt> assembleAuctionArtProjections() {
        return new QAuctionArt(
                auction.id, auction.bidders.highestBidPrice, auction.period.startDate, auction.period.endDate,
                art.id, art.name, art.description, art.price, art.status, art.storageName, art.createdAt,
                owner.id, owner.nickname, owner.school,
                highestBidder.id, highestBidder.nickname, highestBidder.school
        );
    }

    private BooleanExpression highestBidderIdEq(Long memberId) {
        return highestBidder.id.eq(memberId);
    }

    private BooleanExpression auctionIsFinished() {
        return auction.period.endDate.before(LocalDateTime.now());
    }

    private BooleanExpression artIsOnSale() {
        return art.status.eq(ON_SALE);
    }
}

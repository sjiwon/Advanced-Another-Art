package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.assembleAuctionArtProjections;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WinningAuctionArtQueryRepositoryImpl implements WinningAuctionArtQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember owner = new QMember("owner");
    private static final QMember highestBidder = new QMember("highestBidder");

    @Override
    public List<BasicAuctionArt> findWinningAuctionArtListByMemberId(Long memberId) {
        return query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .innerJoin(auction.bidder, highestBidder)
                .where(
                        artTypeEq(AUCTION),
                        highestBidderIdEq(memberId),
                        auctionIsFinished(),
                        artIsNotSold()
                )
                .fetch();
    }

    private BooleanExpression artTypeEq(ArtType saleType) {
        return (saleType != null) ? art.artType.eq(saleType) : null;
    }

    private BooleanExpression highestBidderIdEq(Long memberId) {
        return (memberId != null) ? highestBidder.id.eq(memberId) : null;
    }

    private BooleanExpression auctionIsFinished() {
        return auction.period.endDate.before(LocalDateTime.now());
    }

    private BooleanExpression artIsNotSold() {
        return art.artStatus.eq(ArtStatus.FOR_SALE);
    }
}

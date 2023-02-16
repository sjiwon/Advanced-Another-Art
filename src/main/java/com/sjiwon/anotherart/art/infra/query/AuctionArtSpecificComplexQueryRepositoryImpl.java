package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.utils.SearchCondition;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.assembleAuctionArtProjections;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.orderBySearchCondition;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionArtSpecificComplexQueryRepositoryImpl implements AuctionArtSpecificComplexQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember owner = new QMember("owner");
    private static final QMember highestBidder = new QMember("highestBidder");

    @Override
    public Page<BasicAuctionArt> findCurrentActiveAuctionArtList(SearchCondition condition, Pageable pageRequest) {
        JPAQuery<BasicAuctionArt> basicQuery = query
                .select(assembleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .leftJoin(auction.bidder, highestBidder)
                .where(
                        artTypeEq(AUCTION),
                        currentDateBetween(LocalDateTime.now())
                )
                .orderBy(orderBySearchCondition(condition).toArray(OrderSpecifier[]::new))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        List<BasicAuctionArt> result = completeAuctionArtPagingQuery(basicQuery, condition);
        Long count = query
                .select(art.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(
                        artTypeEq(AUCTION),
                        currentDateBetween(LocalDateTime.now())
                )
                .fetchOne();
        return PageableExecutionUtils.getPage(result, pageRequest, () -> count);
    }

    private List<BasicAuctionArt> completeAuctionArtPagingQuery(JPAQuery<BasicAuctionArt> query, SearchCondition condition) {
        return switch (condition.getSortType()) {
            case LIKE_ASC, LIKE_DESC -> query
                    .leftJoin(favorite).on(favorite.artId.eq(art.id))
                    .groupBy(auctionArtGroupByExpressions())
                    .fetch();
            case BID_COUNT_ASC, BID_COUNT_DESC -> query
                    .leftJoin(auctionRecord).on(auctionRecord.auction.id.eq(auction.id))
                    .groupBy(auctionArtGroupByExpressions())
                    .fetch();
            default -> query.fetch();
        };
    }

    private Expression<?> auctionArtGroupByExpressions() {
        return Expressions.list(
                auction.id, auction.bidAmount, auction.period.startDate, auction.period.endDate,
                highestBidder.id, highestBidder.nickname, highestBidder.school,
                art.id, art.name, art.description, art.price, art.registrationDate, art.uploadImage.storageName,
                owner.id, owner.nickname, owner.school
        );
    }

    private BooleanExpression artTypeEq(ArtType saleType) {
        return (saleType != null) ? art.artType.eq(saleType) : null;
    }

    private BooleanExpression currentDateBetween(LocalDateTime currentDateTime) {
        return (currentDateTime != null) ? auction.period.startDate.before(currentDateTime).and(auction.period.endDate.after(currentDateTime)) : null;
    }
}

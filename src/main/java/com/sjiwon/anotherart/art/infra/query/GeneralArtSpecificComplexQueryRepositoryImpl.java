package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.utils.SearchCondition;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.domain.hashtag.QHashtag.hashtag;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.assembleGeneralArtProjections;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.orderBySearchCondition;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;
import static com.sjiwon.anotherart.purchase.domain.QPurchase.purchase;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GeneralArtSpecificComplexQueryRepositoryImpl implements GeneralArtSpecificComplexQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");

    @Override
    public Page<BasicGeneralArt> findGeneralArtListByKeyword(SearchCondition condition, Pageable pageRequest) {
        JPAQuery<BasicGeneralArt> basicQuery = query
                .select(assembleGeneralArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(
                        artTypeEq(GENERAL),
                        keywordContains(condition.getGivenText())
                )
                .orderBy(orderBySearchCondition(condition).toArray(OrderSpecifier[]::new))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        List<BasicGeneralArt> result = completeGeneralArtPagingQuery(basicQuery, condition);
        Long count = query
                .select(art.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(
                        artTypeEq(GENERAL),
                        keywordContains(condition.getGivenText())
                )
                .fetchOne();
        return PageableExecutionUtils.getPage(result, pageRequest, () -> count);
    }

    @Override
    public Page<BasicGeneralArt> findGeneralArtListByHashtag(SearchCondition condition, Pageable pageRequest) {
        List<Long> artIdWithHashtag = query
                .selectDistinct(art.id)
                .from(art)
                .innerJoin(hashtag).on(hashtag.art.id.eq(art.id))
                .where(hashtag.name.eq(condition.getGivenText()))
                .fetch();

        JPAQuery<BasicGeneralArt> basicQuery = query
                .select(assembleGeneralArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .leftJoin(purchase).on(purchase.art.id.eq(art.id))
                .leftJoin(purchase.buyer, buyer)
                .where(
                        artTypeEq(GENERAL),
                        art.id.in(artIdWithHashtag)
                )
                .orderBy(orderBySearchCondition(condition).toArray(OrderSpecifier[]::new))
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());

        List<BasicGeneralArt> result = completeGeneralArtPagingQuery(basicQuery, condition);
        Long count = query
                .select(art.count())
                .from(art)
                .innerJoin(auction).on(auction.art.id.eq(art.id))
                .where(
                        artTypeEq(GENERAL),
                        art.id.in(artIdWithHashtag)
                )
                .fetchOne();
        return PageableExecutionUtils.getPage(result, pageRequest, () -> count);
    }

    private List<BasicGeneralArt> completeGeneralArtPagingQuery(JPAQuery<BasicGeneralArt> query, SearchCondition condition) {
        return switch (condition.getSortType()) {
            case LIKE_ASC, LIKE_DESC -> query
                    .leftJoin(favorite).on(favorite.artId.eq(art.id))
                    .groupBy(generalArtGroupByExpressions())
                    .fetch();
            default -> query.fetch();
        };
    }

    private Expression<?> generalArtGroupByExpressions() {
        return Expressions.list(
                art.id, art.name, art.description, art.price, art.artStatus, art.registrationDate, art.uploadImage.storageName,
                owner.id, owner.nickname, owner.school,
                buyer.id, buyer.nickname, buyer.school
        );
    }

    private BooleanExpression artTypeEq(ArtType saleType) {
        return (saleType != null) ? art.artType.eq(saleType) : null;
    }

    private BooleanExpression keywordContains(String givenKeyword) {
        return StringUtils.hasText(givenKeyword) ? art.name.contains(givenKeyword).or(art.description.contains(givenKeyword)) : null;
    }
}

package com.sjiwon.anotherart.art.infra.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.SimpleAuctionArt;
import com.sjiwon.anotherart.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.art.utils.ArtQueryFetchingUtils.assembleSimpleAuctionArtProjections;
import static com.sjiwon.anotherart.purchase.domain.QPurchase.purchase;

@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TradedArtQueryRepositoryImpl implements TradedArtQueryRepository {
    private final JPAQueryFactory query;
    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");

    @Override
    public List<SimpleAuctionArt> findSoldAuctionArtListByMemberId(Long memberId) {
        return query
                .select(assembleSimpleAuctionArtProjections())
                .from(art)
                .innerJoin(art.owner, owner)
                .innerJoin(purchase).on(purchase.art.id.eq(art.id))
                .innerJoin(purchase.buyer, buyer)
                .where(
                        artTypeEq(AUCTION),
                        ownerIdEq(memberId)
                )
                .fetch();
    }

    private BooleanExpression artTypeEq(ArtType saleType) {
        return (saleType != null) ? art.artType.eq(saleType) : null;
    }

    private BooleanExpression ownerIdEq(Long memberId) {
        return (memberId != null) ? art.owner.id.eq(memberId) : null;
    }
}

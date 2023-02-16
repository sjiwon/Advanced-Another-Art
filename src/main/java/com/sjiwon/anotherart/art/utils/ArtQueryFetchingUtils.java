package com.sjiwon.anotherart.art.utils;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.infra.query.dto.QBasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.QBasicGeneralArt;
import com.sjiwon.anotherart.member.domain.QMember;

import java.util.LinkedList;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;
import static com.sjiwon.anotherart.auction.domain.record.QAuctionRecord.auctionRecord;
import static com.sjiwon.anotherart.favorite.domain.QFavorite.favorite;

public class ArtQueryFetchingUtils {
    private static final QMember owner = new QMember("owner");
    private static final QMember buyer = new QMember("buyer");
    private static final QMember highestBidder = new QMember("highestBidder");

    public static ConstructorExpression<BasicGeneralArt> assembleGeneralArtProjections() {
        return new QBasicGeneralArt(
                art.id, art.name, art.description, art.price, art.artStatus, art.registrationDate, art.uploadImage.storageName,
                owner.id, owner.nickname, owner.school,
                buyer.id, buyer.nickname, buyer.school
        );
    }

    public static ConstructorExpression<BasicAuctionArt> assembleAuctionArtProjections() {
        return new QBasicAuctionArt(
                auction.id, auction.bidAmount, auction.period.startDate, auction.period.endDate,
                highestBidder.id, highestBidder.nickname, highestBidder.school,
                art.id, art.name, art.description, art.price, art.registrationDate, art.uploadImage.storageName,
                owner.id, owner.nickname, owner.school
        );
    }

    public static List<OrderSpecifier<?>> orderBySearchCondition(SearchCondition condition) {
        List<OrderSpecifier<?>> orderBy = new LinkedList<>();
        ArtType artType = condition.getArtType();

        switch (condition.getSortType()) {
            case DATE_ASC -> orderBy.add(art.registrationDate.asc());
            case DATE_DESC -> orderBy.add(art.registrationDate.desc());
            case PRICE_ASC -> orderBy.add(artType == AUCTION ? auction.bidAmount.asc() : art.price.asc());
            case PRICE_DESC -> orderBy.add(artType == AUCTION ? auction.bidAmount.desc() : art.price.desc());
            case LIKE_ASC -> orderBy.add(favorite.count().asc());
            case LIKE_DESC -> orderBy.add(favorite.count().desc());
            case BID_COUNT_ASC -> orderBy.add(auctionRecord.count().asc());
            default -> orderBy.add(auctionRecord.count().desc());
        }
        orderBy.add(orderByArtIdDesc());
        return orderBy;
    }

    private static OrderSpecifier<Long> orderByArtIdDesc() {
        return art.id.desc();
    }
}

package com.sjiwon.anotherart.art.utils;

import com.querydsl.core.types.ConstructorExpression;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.infra.query.dto.QBasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.QBasicGeneralArt;
import com.sjiwon.anotherart.member.domain.QMember;

import static com.sjiwon.anotherart.art.domain.QArt.art;
import static com.sjiwon.anotherart.auction.domain.QAuction.auction;

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
}

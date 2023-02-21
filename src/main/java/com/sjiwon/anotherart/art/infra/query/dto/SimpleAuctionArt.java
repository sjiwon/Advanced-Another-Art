package com.sjiwon.anotherart.art.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SimpleAuctionArt {
    // 판매자 정보
    private final Long ownerId;
    private final String ownerNickname;
    private final String ownerSchool;

    // 구매자 정보
    private final Long buyerId;
    private final String buyerNickname;
    private final String buyerSchool;

    // 작품 정보
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final int purchasePrice;
    private final String artStorageName;

    @Builder
    @QueryProjection
    public SimpleAuctionArt(
            Long ownerId, String ownerNickname, String ownerSchool,
            Long buyerId, String buyerNickname, String buyerSchool,
            Long artId, String artName, String artDescription, int purchasePrice, String artStorageName) {
        this.ownerId = ownerId;
        this.ownerNickname = ownerNickname;
        this.ownerSchool = ownerSchool;
        this.buyerId = buyerId;
        this.buyerNickname = buyerNickname;
        this.buyerSchool = buyerSchool;
        this.artId = artId;
        this.artName = artName;
        this.artDescription = artDescription;
        this.purchasePrice = purchasePrice;
        this.artStorageName = artStorageName;
    }
}

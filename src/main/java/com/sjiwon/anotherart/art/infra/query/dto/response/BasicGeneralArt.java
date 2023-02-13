package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BasicGeneralArt {
    // 작품 정보
    private final Long artId;
    private final String artName;
    private final String artDescription;
    private final int artPrice;
    private final ArtStatus artStatus;
    private final LocalDateTime artRegisterDate;
    private final String artStorageName;

    // 작품 주인 정보 - NotNull
    private final Long ownerId;
    private final String ownerNickname;
    private final String ownerSchool;

    // 구매자 정보 - Nullable
    private final Long buyerId;
    private final String buyerNickname;
    private final String buyerSchool;

    @Builder
    @QueryProjection
    public BasicGeneralArt(
            Long artId, String artName, String artDescription, int artPrice, ArtStatus artStatus, LocalDateTime artRegisterDate, String artStorageName,
            Long ownerId, String ownerNickname, String ownerSchool,
            Long buyerId, String buyerNickname, String buyerSchool
    ) {
        this.artId = artId;
        this.artName = artName;
        this.artDescription = artDescription;
        this.artPrice = artPrice;
        this.artStatus = artStatus;
        this.artRegisterDate = artRegisterDate;
        this.artStorageName = artStorageName;
        this.ownerId = ownerId;
        this.ownerNickname = ownerNickname;
        this.ownerSchool = ownerSchool;
        this.buyerId = buyerId;
        this.buyerNickname = buyerNickname;
        this.buyerSchool = buyerSchool;
    }
}

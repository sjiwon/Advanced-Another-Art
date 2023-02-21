package com.sjiwon.anotherart.common.utils;

import com.sjiwon.anotherart.art.infra.query.dto.SimpleAuctionArt;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;

import java.util.UUID;

public class SimpleArtBuilder {
    public static SimpleAuctionArt createSimpleAuctionArt(ArtFixture art) {
        return SimpleAuctionArt.builder()
                .ownerId(1L)
                .ownerNickname(MemberFixture.A.getNickname())
                .ownerSchool("경기대학교")
                .buyerId(2L)
                .buyerNickname(MemberFixture.B.getNickname())
                .buyerSchool("서울대학교")
                .artId(1L)
                .artName(art.getName())
                .artDescription(art.getDescription())
                .purchasePrice(art.getPrice())
                .artStorageName(generateStorageName())
                .build();
    }

    private static String generateStorageName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + ".png";
    }
}

package com.sjiwon.anotherart.art.controller.utils;

import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.sjiwon.anotherart.common.utils.ArtUtils.currentTime1DayAgo;
import static com.sjiwon.anotherart.common.utils.ArtUtils.currentTime3DayLater;

public class BasicArtBuilder {
    public static BasicGeneralArt createBasicGeneralArt(ArtFixture art) {
        return BasicGeneralArt.builder()
                .artId(1L)
                .artName(art.getName())
                .artDescription(art.getDescription())
                .artPrice(art.getPrice())
                .artStatus(ArtStatus.SOLD_OUT)
                .artRegistrationDate(currentTime1DayAgo)
                .artStorageName(generateStorageName())
                .ownerId(1L)
                .ownerNickname(MemberFixture.A.getNickname())
                .ownerSchool("경기대학교")
                .buyerId(1L)
                .buyerNickname(MemberFixture.B.getNickname())
                .buyerSchool("서울대학교")
                .build();
    }

    public static BasicAuctionArt createBasicAuctionArt(ArtFixture art) {
        return BasicAuctionArt.builder()
                .auctionId(1L)
                .highestBidPrice(art.getPrice() + 5_000)
                .auctionStartDate(currentTime1DayAgo)
                .auctionEndDate(currentTime3DayLater)
                .highestBidderId(2L)
                .highestBidderNickname(MemberFixture.B.getNickname())
                .highestBidderSchool("서울대학교")
                .artId(1L)
                .artName(art.getName())
                .artDescription(art.getDescription())
                .artPrice(art.getPrice())
                .artRegistrationDate(LocalDateTime.now().minusDays(1))
                .artStorageName(generateStorageName())
                .ownerId(1L)
                .ownerNickname(MemberFixture.A.getNickname())
                .ownerSchool("경기대학교")
                .build();
    }

    private static String generateStorageName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + ".png";
    }
}

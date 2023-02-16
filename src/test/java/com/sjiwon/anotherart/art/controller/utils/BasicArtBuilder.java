package com.sjiwon.anotherart.art.controller.utils;

import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;

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

    public static List<AuctionArt> createAuctionArtList(int totalElements) {
        List<BasicAuctionArt> basicAuctionArts = new ArrayList<>();
        int bidPrice = 1_000;
        for (int i = 1; i <= totalElements; i++) {
            basicAuctionArts.add(
                    BasicAuctionArt.builder()
                            .auctionId(1L)
                            .highestBidPrice(bidPrice)
                            .auctionStartDate(currentTime1DayAgo)
                            .auctionEndDate(currentTime3DayLater)
                            .highestBidderId(2L)
                            .highestBidderNickname(MemberFixture.B.getNickname())
                            .highestBidderSchool("서울대학교")
                            .artId((long) i)
                            .artName("name" + i)
                            .artDescription("name" + i)
                            .artPrice(1_000)
                            .artRegistrationDate(LocalDateTime.now().minusDays(30 - i))
                            .artStorageName(generateStorageName())
                            .ownerId(1L)
                            .ownerNickname(MemberFixture.A.getNickname())
                            .ownerSchool("경기대학교")
                            .build()
            );
            bidPrice += 1_000;
        }

        List<AuctionArt> auctionArts = new ArrayList<>();
        for (int i = 1; i <= totalElements; i++) {
            auctionArts.add(
                    AuctionArt.builder()
                            .art(basicAuctionArts.get(i - 1))
                            .hashtags(HASHTAGS)
                            .likeMarkingMembers(LongStream.rangeClosed(1, i).boxed().collect(Collectors.toList()))
                            .bidCount(i)
                            .build()
            );
        }
        return auctionArts;
    }

    private static String generateStorageName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + ".png";
    }
}

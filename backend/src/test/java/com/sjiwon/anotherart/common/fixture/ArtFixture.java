package com.sjiwon.anotherart.common.fixture;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.domain.model.UploadImage;
import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.sjiwon.anotherart.art.domain.model.Art.Type.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.Art.Type.GENERAL;

@Getter
@RequiredArgsConstructor
public enum ArtFixture {
    AUCTION_1(
            ArtName.from("경매 작품 1"), Description.from("경매 작품 1"),
            AUCTION, 100_000, new UploadImage("1.png", "S3/1.png"),
            new HashSet<>(Set.of("AUCTION_1 A", "AUCTION_1 B", "AUCTION_1 C"))
    ),
    AUCTION_2(
            ArtName.from("경매 작품 2"), Description.from("경매 작품 2"),
            AUCTION, 110_000, new UploadImage("2.png", "S3/2.png"),
            new HashSet<>(Set.of("AUCTION_2 A", "AUCTION_2 B", "AUCTION_2 C"))
    ),
    AUCTION_3(
            ArtName.from("경매 작품 3"), Description.from("경매 작품 3"),
            AUCTION, 120_000, new UploadImage("3.png", "S3/3.png"),
            new HashSet<>(Set.of("AUCTION_3 A", "AUCTION_3 B", "AUCTION_3 C"))
    ),
    AUCTION_4(
            ArtName.from("경매 작품 4"), Description.from("경매 작품 4"),
            AUCTION, 130_000, new UploadImage("4.png", "S3/4.png"),
            new HashSet<>(Set.of("AUCTION_4 A", "AUCTION_4 B", "AUCTION_4 C"))
    ),
    AUCTION_5(
            ArtName.from("경매 작품 5"), Description.from("경매 작품 5"),
            AUCTION, 140_000, new UploadImage("5.png", "S3/5.png"),
            new HashSet<>(Set.of("AUCTION_5 A", "AUCTION_5 B", "AUCTION_5 C"))
    ),
    AUCTION_6(
            ArtName.from("경매 작품 6"), Description.from("경매 작품 6"),
            AUCTION, 150_000, new UploadImage("6.png", "S3/6.png"),
            new HashSet<>(Set.of("AUCTION_6 A", "AUCTION_6 B", "AUCTION_6 C"))
    ),
    AUCTION_7(
            ArtName.from("경매 작품 7"), Description.from("경매 작품 7"),
            AUCTION, 160_000, new UploadImage("7.png", "S3/7.png"),
            new HashSet<>(Set.of("AUCTION_7 A", "AUCTION_7 B", "AUCTION_7 C"))
    ),
    AUCTION_8(
            ArtName.from("경매 작품 8"), Description.from("경매 작품 8"),
            AUCTION, 170_000, new UploadImage("8.png", "S3/8.png"),
            new HashSet<>(Set.of("AUCTION_8 A", "AUCTION_8 B", "AUCTION_8 C"))
    ),
    AUCTION_9(
            ArtName.from("경매 작품 9"), Description.from("경매 작품 9"),
            AUCTION, 180_000, new UploadImage("9.png", "S3/9.png"),
            new HashSet<>(Set.of("AUCTION_9 A", "AUCTION_9 B", "AUCTION_9 C"))
    ),
    AUCTION_10(
            ArtName.from("경매 작품 10"), Description.from("경매 작품 10"),
            AUCTION, 190_000, new UploadImage("10.png", "S3/10.png"),
            new HashSet<>(Set.of("AUCTION_10 A", "AUCTION_10 B", "AUCTION_10 C"))
    ),
    AUCTION_11(
            ArtName.from("경매 작품 11"), Description.from("경매 작품 11"),
            AUCTION, 200_000, new UploadImage("11.png", "S3/11.png"),
            new HashSet<>(Set.of("AUCTION_11 A", "AUCTION_11 B", "AUCTION_11 C"))
    ),
    AUCTION_12(
            ArtName.from("경매 작품 12"), Description.from("경매 작품 12"),
            AUCTION, 190_000, new UploadImage("12.png", "S3/12.png"),
            new HashSet<>(Set.of("AUCTION_12 A", "AUCTION_12 B", "AUCTION_12 C"))
    ),
    AUCTION_13(
            ArtName.from("경매 작품 13"), Description.from("경매 작품 13"),
            AUCTION, 180_000, new UploadImage("13.png", "S3/13.png"),
            new HashSet<>(Set.of("AUCTION_13 A", "AUCTION_13 B", "AUCTION_13 C"))
    ),
    AUCTION_14(
            ArtName.from("경매 작품 14"), Description.from("경매 작품 14"),
            AUCTION, 170_000, new UploadImage("14.png", "S3/14.png"),
            new HashSet<>(Set.of("AUCTION_14 A", "AUCTION_14 B", "AUCTION_14 C"))
    ),
    AUCTION_15(
            ArtName.from("경매 작품 15"), Description.from("경매 작품 15"),
            AUCTION, 160_000, new UploadImage("15.png", "S3/15.png"),
            new HashSet<>(Set.of("AUCTION_15 A", "AUCTION_15 B", "AUCTION_15 C"))
    ),
    AUCTION_16(
            ArtName.from("경매 작품 16"), Description.from("경매 작품 16"),
            AUCTION, 150_000, new UploadImage("16.png", "S3/16.png"),
            new HashSet<>(Set.of("AUCTION_16 A", "AUCTION_16 B", "AUCTION_16 C"))
    ),
    AUCTION_17(
            ArtName.from("경매 작품 17"), Description.from("경매 작품 17"),
            AUCTION, 140_000, new UploadImage("17.png", "S3/17.png"),
            new HashSet<>(Set.of("AUCTION_17 A", "AUCTION_17 B", "AUCTION_17 C"))
    ),
    AUCTION_18(
            ArtName.from("경매 작품 18"), Description.from("경매 작품 18"),
            AUCTION, 130_000, new UploadImage("18.png", "S3/18.png"),
            new HashSet<>(Set.of("AUCTION_18 A", "AUCTION_18 B", "AUCTION_18 C"))
    ),
    AUCTION_19(
            ArtName.from("경매 작품 19"), Description.from("경매 작품 19"),
            AUCTION, 120_000, new UploadImage("19.png", "S3/19.png"),
            new HashSet<>(Set.of("AUCTION_19 A", "AUCTION_19 B", "AUCTION_19 C"))
    ),
    AUCTION_20(
            ArtName.from("경매 작품 20"), Description.from("경매 작품 20"),
            AUCTION, 110_000, new UploadImage("20.png", "S3/20.png"),
            new HashSet<>(Set.of("AUCTION_20 A", "AUCTION_20 B", "AUCTION_20 C"))
    ),

    GENERAL_1(
            ArtName.from("일반 작품 1"), Description.from("일반 작품 1"),
            GENERAL, 100_000, new UploadImage("21.png", "S3/21.png"),
            new HashSet<>(Set.of("GENERAL_1 A", "GENERAL_1 B", "GENERAL_1 C"))
    ),
    GENERAL_2(
            ArtName.from("일반 작품 2"), Description.from("일반 작품 2"),
            GENERAL, 110_000, new UploadImage("22.png", "S3/22.png"),
            new HashSet<>(Set.of("GENERAL_2 A", "GENERAL_2 B", "GENERAL_2 C"))
    ),
    GENERAL_3(
            ArtName.from("일반 작품 3"), Description.from("일반 작품 3"),
            GENERAL, 120_000, new UploadImage("23.png", "S3/23.png"),
            new HashSet<>(Set.of("GENERAL_3 A", "GENERAL_3 B", "GENERAL_3 C"))
    ),
    GENERAL_4(
            ArtName.from("일반 작품 4"), Description.from("일반 작품 4"),
            GENERAL, 130_000, new UploadImage("24.png", "S3/24.png"),
            new HashSet<>(Set.of("GENERAL_4 A", "GENERAL_4 B", "GENERAL_4 C"))
    ),
    GENERAL_5(
            ArtName.from("일반 작품 5"), Description.from("일반 작품 5"),
            GENERAL, 140_000, new UploadImage("25.png", "S3/25.png"),
            new HashSet<>(Set.of("GENERAL_5 A", "GENERAL_5 B", "GENERAL_5 C"))
    ),
    GENERAL_6(
            ArtName.from("일반 작품 6"), Description.from("일반 작품 6"),
            GENERAL, 150_000, new UploadImage("26.png", "S3/26.png"),
            new HashSet<>(Set.of("GENERAL_6 A", "GENERAL_6 B", "GENERAL_6 C"))
    ),
    GENERAL_7(
            ArtName.from("일반 작품 7"), Description.from("일반 작품 7"),
            GENERAL, 160_000, new UploadImage("27.png", "S3/27.png"),
            new HashSet<>(Set.of("GENERAL_7 A", "GENERAL_7 B", "GENERAL_7 C"))
    ),
    GENERAL_8(
            ArtName.from("일반 작품 8"), Description.from("일반 작품 8"),
            GENERAL, 170_000, new UploadImage("28.png", "S3/28.png"),
            new HashSet<>(Set.of("GENERAL_8 A", "GENERAL_8 B", "GENERAL_8 C"))
    ),
    GENERAL_9(
            ArtName.from("일반 작품 9"), Description.from("일반 작품 9"),
            GENERAL, 180_000, new UploadImage("29.png", "S3/29.png"),
            new HashSet<>(Set.of("GENERAL_9 A", "GENERAL_9 B", "GENERAL_9 C"))
    ),
    GENERAL_10(
            ArtName.from("일반 작품 10"), Description.from("일반 작품 10"),
            GENERAL, 190_000, new UploadImage("30.png", "S3/30.png"),
            new HashSet<>(Set.of("GENERAL_10 A", "GENERAL_10 B", "GENERAL_10 C"))
    ),
    GENERAL_11(
            ArtName.from("일반 작품 11"), Description.from("일반 작품 11"),
            GENERAL, 200_000, new UploadImage("31.png", "S3/31.png"),
            new HashSet<>(Set.of("GENERAL_11 A", "GENERAL_11 B", "GENERAL_11 C"))
    ),
    GENERAL_12(
            ArtName.from("일반 작품 12"), Description.from("일반 작품 12"),
            GENERAL, 190_000, new UploadImage("32.png", "S3/32.png"),
            new HashSet<>(Set.of("GENERAL_12 A", "GENERAL_12 B", "GENERAL_12 C"))
    ),
    GENERAL_13(
            ArtName.from("일반 작품 13"), Description.from("일반 작품 13"),
            GENERAL, 180_000, new UploadImage("33.png", "S3/33.png"),
            new HashSet<>(Set.of("GENERAL_13 A", "GENERAL_13 B", "GENERAL_13 C"))
    ),
    GENERAL_14(
            ArtName.from("일반 작품 14"), Description.from("일반 작품 14"),
            GENERAL, 170_000, new UploadImage("34.png", "S3/34.png"),
            new HashSet<>(Set.of("GENERAL_14 A", "GENERAL_14 B", "GENERAL_14 C"))
    ),
    GENERAL_15(
            ArtName.from("일반 작품 15"), Description.from("일반 작품 15"),
            GENERAL, 160_000, new UploadImage("35.png", "S3/35.png"),
            new HashSet<>(Set.of("GENERAL_15 A", "GENERAL_15 B", "GENERAL_15 C"))
    ),
    GENERAL_16(
            ArtName.from("일반 작품 16"), Description.from("일반 작품 16"),
            GENERAL, 150_000, new UploadImage("36.png", "S3/36.png"),
            new HashSet<>(Set.of("GENERAL_16 A", "GENERAL_16 B", "GENERAL_16 C"))
    ),
    GENERAL_17(
            ArtName.from("일반 작품 17"), Description.from("일반 작품 17"),
            GENERAL, 140_000, new UploadImage("37.png", "S3/37.png"),
            new HashSet<>(Set.of("GENERAL_17 A", "GENERAL_17 B", "GENERAL_17 C"))
    ),
    GENERAL_18(
            ArtName.from("일반 작품 18"), Description.from("일반 작품 18"),
            GENERAL, 130_000, new UploadImage("38.png", "S3/38.png"),
            new HashSet<>(Set.of("GENERAL_18 A", "GENERAL_18 B", "GENERAL_18 C"))
    ),
    GENERAL_19(
            ArtName.from("일반 작품 19"), Description.from("일반 작품 19"),
            GENERAL, 120_000, new UploadImage("39.png", "S3/39.png"),
            new HashSet<>(Set.of("GENERAL_19 A", "GENERAL_19 B", "GENERAL_19 C"))
    ),
    GENERAL_20(
            ArtName.from("일반 작품 20"), Description.from("일반 작품 20"),
            GENERAL, 110_000, new UploadImage("40.png", "S3/40.png"),
            new HashSet<>(Set.of("GENERAL_20A", "GENERAL_20 B", "GENERAL_20 C"))
    ),
    ;

    private final ArtName name;
    private final Description description;
    private final Art.Type type;
    private final int price;
    private final UploadImage uploadImage;
    private final Set<String> hashtags;

    public Art toDomain(final Member owner) {
        return Art.createArt(
                owner,
                name,
                description,
                type,
                price,
                uploadImage,
                hashtags
        );
    }

    public Art toDomain(final Member owner, final int price) {
        return Art.createArt(
                owner,
                name,
                description,
                type,
                price,
                uploadImage,
                hashtags
        );
    }

    public Art toDomain(final Member owner, final String keyword, final Set<String> hashtags) {
        return Art.createArt(
                owner,
                ArtName.from(keyword),
                Description.from(keyword),
                type,
                price,
                uploadImage,
                hashtags
        );
    }
}

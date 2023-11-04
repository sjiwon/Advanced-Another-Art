package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.domain.Description;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;

@Getter
@RequiredArgsConstructor
public enum ArtFixture {
    AUCTION_1("경매 작품 1", "경매 작품 1", AUCTION, 100_000, "1.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_2("경매 작품 2", "경매 작품 2", AUCTION, 100_000, "2.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_3("경매 작품 3", "경매 작품 3", AUCTION, 100_000, "3.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_4("경매 작품 4", "경매 작품 4", AUCTION, 100_000, "4.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_5("경매 작품 5", "경매 작품 5", AUCTION, 100_000, "5.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_6("경매 작품 6", "경매 작품 6", AUCTION, 100_000, "6.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_7("경매 작품 7", "경매 작품 7", AUCTION, 100_000, "7.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_8("경매 작품 8", "경매 작품 8", AUCTION, 100_000, "8.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_9("경매 작품 9", "경매 작품 9", AUCTION, 100_000, "9.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_10("경매 작품 10", "경매 작품 10", AUCTION, 100_000, "10.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_11("경매 작품 11", "경매 작품 11", AUCTION, 100_000, "11.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_12("경매 작품 12", "경매 작품 12", AUCTION, 100_000, "12.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_13("경매 작품 13", "경매 작품 13", AUCTION, 100_000, "13.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_14("경매 작품 14", "경매 작품 14", AUCTION, 100_000, "14.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_15("경매 작품 15", "경매 작품 15", AUCTION, 100_000, "15.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_16("경매 작품 16", "경매 작품 16", AUCTION, 100_000, "16.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_17("경매 작품 17", "경매 작품 17", AUCTION, 100_000, "17.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_18("경매 작품 18", "경매 작품 18", AUCTION, 100_000, "18.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_19("경매 작품 19", "경매 작품 19", AUCTION, 100_000, "19.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_20("경매 작품 20", "경매 작품 20", AUCTION, 100_000, "20.png", new HashSet<>(Set.of("A", "B", "C"))),

    GENERAL_1("일반 작품 1", "일반 작품 1", GENERAL, 100_000, "1.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_2("일반 작품 2", "일반 작품 2", GENERAL, 100_000, "2.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_3("일반 작품 3", "일반 작품 3", GENERAL, 100_000, "3.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_4("일반 작품 4", "일반 작품 4", GENERAL, 100_000, "4.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_5("일반 작품 5", "일반 작품 5", GENERAL, 100_000, "5.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_6("일반 작품 6", "일반 작품 6", GENERAL, 100_000, "6.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_7("일반 작품 7", "일반 작품 7", GENERAL, 100_000, "7.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_8("일반 작품 8", "일반 작품 8", GENERAL, 100_000, "8.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_9("일반 작품 9", "일반 작품 9", GENERAL, 100_000, "9.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_10("일반 작품 10", "일반 작품 10", GENERAL, 100_000, "10.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_11("일반 작품 11", "일반 작품 11", GENERAL, 100_000, "11.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_12("일반 작품 12", "일반 작품 12", GENERAL, 100_000, "12.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_13("일반 작품 13", "일반 작품 13", GENERAL, 100_000, "13.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_14("일반 작품 14", "일반 작품 14", GENERAL, 100_000, "14.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_15("일반 작품 15", "일반 작품 15", GENERAL, 100_000, "15.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_16("일반 작품 16", "일반 작품 16", GENERAL, 100_000, "16.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_17("일반 작품 17", "일반 작품 17", GENERAL, 100_000, "17.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_18("일반 작품 18", "일반 작품 18", GENERAL, 100_000, "18.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_19("일반 작품 19", "일반 작품 19", GENERAL, 100_000, "19.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_20("일반 작품 20", "일반 작품 20", GENERAL, 100_000, "20.png", new HashSet<>(Set.of("A", "B", "C"))),
    ;

    private final String name;
    private final String description;
    private final ArtType type;
    private final int price;
    private final String storageName;
    private final Set<String> hashtags;

    public Art toArt(final Member owner) {
        return Art.createArt(
                owner,
                ArtName.from(name),
                Description.from(description),
                type,
                price,
                storageName,
                hashtags
        );
    }

    public Art toArt(final Member owner, final int price) {
        return Art.createArt(
                owner,
                ArtName.from(name),
                Description.from(description),
                type,
                price,
                storageName,
                hashtags
        );
    }

    public Art toArt(final Member owner, final String keyword, final Set<String> hashtags) {
        return Art.createArt(
                owner,
                ArtName.from(keyword),
                Description.from(keyword),
                type,
                price,
                storageName,
                hashtags
        );
    }
}

package com.sjiwon.anotherart.common.fixture;

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

    GENERAL_1("일반 작품 1", "일반 작품 1", GENERAL, 100_000, "6.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_2("일반 작품 2", "일반 작품 2", GENERAL, 100_000, "7.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_3("일반 작품 3", "일반 작품 3", GENERAL, 100_000, "8.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_4("일반 작품 4", "일반 작품 4", GENERAL, 100_000, "9.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_5("일반 작품 5", "일반 작품 5", GENERAL, 100_000, "10.png", new HashSet<>(Set.of("A", "B", "C"))),
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

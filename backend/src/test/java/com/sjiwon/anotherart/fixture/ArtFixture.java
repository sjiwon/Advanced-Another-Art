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
    AUCTION_A("경매 작품 A", "경매 작품 A", AUCTION, 100_000, "A.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_B("경매 작품 B", "경매 작품 B", AUCTION, 100_000, "B.png", new HashSet<>(Set.of("A", "B", "C"))),
    AUCTION_C("경매 작품 C", "경매 작품 C", AUCTION, 100_000, "C.png", new HashSet<>(Set.of("A", "B", "C"))),

    GENERAL_A("일반 작품 A", "일반 작품 A", GENERAL, 100_000, "A.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_B("일반 작품 B", "일반 작품 B", GENERAL, 100_000, "B.png", new HashSet<>(Set.of("A", "B", "C"))),
    GENERAL_C("일반 작품 C", "일반 작품 C", GENERAL, 100_000, "C.png", new HashSet<>(Set.of("A", "B", "C"))),
    ;

    private final String name;
    private final String description;
    private final ArtType type;
    private final int price;
    private final String storageName;
    private final Set<String> hashtags;

    public Art toArt(Member owner) {
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
}

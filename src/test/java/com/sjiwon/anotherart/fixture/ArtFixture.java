package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum ArtFixture {
    A("A", "A", ArtType.AUCTION, 100000, "A.png"),
    B("B", "B", ArtType.GENERAL, 100000, "B.png"),
    C("C", "C", ArtType.AUCTION, 100000, "C.png"),
    ;

    private final String name;
    private final String description;
    private final ArtType artType;
    private final int price;
    private final String uploadName;

    public Art toArt(Member member) {
        return Art.builder()
                .name(name)
                .description(description)
                .artType(artType)
                .price(price)
                .uploadImage(UploadImage.of(uploadName, generateRandomStorageName()))
                .member(member)
                .build();
    }

    private String generateRandomStorageName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }
}

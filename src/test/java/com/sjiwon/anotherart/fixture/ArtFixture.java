package com.sjiwon.anotherart.fixture;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.common.utils.ArtUtils;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public enum ArtFixture {
    A("A", "A", ArtType.AUCTION, "A.png"),
    B("B", "B", ArtType.GENERAL, "B.png"),
    B_BMP("B", "B", ArtType.GENERAL, "B_BMP.bmp"),
    C("C", "C", ArtType.AUCTION, "C.png"),
    ;

    private final String name;
    private final String description;
    private final ArtType artType;
    private final int price = ArtUtils.INIT_PRICE;
    private final String uploadName;

    public Art toArt(Member artOwner, List<String> hashtags) {
        return Art.builder()
                .owner(artOwner)
                .name(name)
                .description(description)
                .artType(artType)
                .price(price)
                .uploadImage(UploadImage.of(uploadName, generateRandomStorageName()))
                .hashtags(new HashSet<>(hashtags))
                .build();
    }

    private String generateRandomStorageName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }
}

package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ArtType {
    GENERAL("일반 작품", "general"),
    AUCTION("경매 작품", "auction"),
    ;

    private final String description;
    private final String brief;

    public static ArtType from(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.brief.equals(value))
                .findFirst()
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.INVALID_ART_TYPE));
    }
}

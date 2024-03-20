package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.INVALID_ART_TYPE;

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
                .orElseThrow(() -> new ArtException(INVALID_ART_TYPE));
    }

    public boolean isAuctionType() {
        return this == AUCTION;
    }
}

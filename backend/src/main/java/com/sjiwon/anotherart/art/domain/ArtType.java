package com.sjiwon.anotherart.art.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtType {
    GENERAL("일반 작품"),
    AUCTION("경매 작품"),
    ;

    private final String description;
}

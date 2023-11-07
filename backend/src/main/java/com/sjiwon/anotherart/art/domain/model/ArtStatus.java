package com.sjiwon.anotherart.art.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtStatus {
    ON_SALE("판매중"),
    SOLD("판매 완료"),
    ;

    private final String description;
}

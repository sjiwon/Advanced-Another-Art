package com.sjiwon.anotherart.point.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    JOIN("신규 가입"),
    CHARGE("충전"),
    REFUND("환불"),
    PURCHASE("작품 구매"),
    SOLD("작품 판매")
    ;

    private final String description;
}

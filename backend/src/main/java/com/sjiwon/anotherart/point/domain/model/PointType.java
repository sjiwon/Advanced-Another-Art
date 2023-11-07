package com.sjiwon.anotherart.point.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    CHARGE("포인트 충전", true),
    REFUND("포인트 환불", false),
    PURCHASE("작품 구매", false),
    SOLD("작품 판매", true),
    ;

    private final String description;
    private final boolean increaseType;
}

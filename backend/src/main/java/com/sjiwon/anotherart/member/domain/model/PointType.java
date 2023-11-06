package com.sjiwon.anotherart.member.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    CHARGE("포인트 충전") {
        @Override
        public boolean isIncreaseType() {
            return true;
        }
    },
    REFUND("포인트 환불"),
    PURCHASE("작품 구매"),
    SOLD("작품 판매") {
        @Override
        public boolean isIncreaseType() {
            return true;
        }
    },
    ;

    private final String description;

    public boolean isIncreaseType() {
        return false;
    }
}

package com.sjiwon.anotherart.member.domain.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    CHARGE("충전") {
        @Override
        public boolean isIncreaseType() {
            return true;
        }
    },
    REFUND("환불"),
    PURCHASE("구매"),
    SOLD("판매") {
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

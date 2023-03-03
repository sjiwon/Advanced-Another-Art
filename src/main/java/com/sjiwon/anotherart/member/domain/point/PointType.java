package com.sjiwon.anotherart.member.domain.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PointType {
    CHARGE("충전") {
        @Override
        public boolean isChargeType() {
            return true;
        }
    },
    REFUND("환불") {
        @Override
        public boolean isRefundType() {
            return true;
        }
    },
    PURCHASE("작품 구매"),
    SOLD("작품 판매")
    ;

    private final String description;

    public boolean isChargeType() {
        return false;
    }

    public boolean isRefundType() {
        return false;
    }
}

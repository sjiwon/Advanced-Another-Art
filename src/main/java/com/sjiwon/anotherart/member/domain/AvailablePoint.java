package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class AvailablePoint {
    @Column(name = "available_point", nullable = false)
    private int value;

    private AvailablePoint(int value) {
        this.value = value;
    }

    public static AvailablePoint from(int value) {
        validatePointAmount(value);
        return new AvailablePoint(value);
    }

    public AvailablePoint increasePoint(int value) {
        validatePointAmount(value);
        return new AvailablePoint(this.value + value);
    }

    public AvailablePoint decreasePoint(int value) {
        validatePointAmount(value);
        verifyDecreaseRequest(value);
        return new AvailablePoint(this.value - value);
    }

    private static void validatePointAmount(int value) {
        if (value < 0) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_POINT_RANGE);
        }
    }

    private void verifyDecreaseRequest(int value) {
        if (this.value - value < 0) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_POINT_DECREASE);
        }
    }
}

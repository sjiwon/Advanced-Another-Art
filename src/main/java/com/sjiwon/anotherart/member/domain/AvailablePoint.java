package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.member.exception.MemberException;
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

    public AvailablePoint update(int value) {
        validatePointAmount(value);
        verifyUpdateRequest(value);
        return new AvailablePoint(value);
    }

    private static void validatePointAmount(int value) {
        if (value < 0) {
            throw MemberException.type(MemberErrorCode.INVALID_AVAILABLE_POINTS);
        }
    }

    private void verifyUpdateRequest(int value) {
        if (this.value - value < 0) {
            throw MemberException.type(MemberErrorCode.INVALID_POINT_UPDATE);
        }
    }
}

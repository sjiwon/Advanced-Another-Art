package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.POINT_CANNOT_BE_NEGATIVE;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.POINT_IS_NOT_ENOUGH;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Point {
    private static final int INIT_POINT = 0;

    @Column(name = "total_point", nullable = false)
    private int totalPoint;

    @Column(name = "available_point", nullable = false)
    private int availablePoint;

    private Point(final int totalPoint, final int availablePoint) {
        this.totalPoint = totalPoint;
        this.availablePoint = availablePoint;
    }

    public static Point init() {
        return new Point(INIT_POINT, INIT_POINT);
    }

    public Point increaseTotalPoint(final int value) {
        validatePointIsPositive(value);
        return new Point(totalPoint + value, availablePoint + value);
    }

    public Point decreaseTotalPoint(final int value) {
        validatePointIsPositive(value);
        validatePointIsEnough(value);
        return new Point(totalPoint - value, availablePoint - value);
    }

    public Point increaseAvailablePoint(final int value) {
        validatePointIsPositive(value);
        return new Point(totalPoint, availablePoint + value);
    }

    public Point decreaseAvailablePoint(final int value) {
        validatePointIsPositive(value);
        validatePointIsEnough(value);
        return new Point(totalPoint, availablePoint - value);
    }

    private static void validatePointIsPositive(final int value) {
        if (value < 0) {
            throw new MemberException(POINT_CANNOT_BE_NEGATIVE);
        }
    }

    private void validatePointIsEnough(final int value) {
        if (totalPoint < value || availablePoint < value) {
            throw new MemberException(POINT_IS_NOT_ENOUGH);
        }
    }
}

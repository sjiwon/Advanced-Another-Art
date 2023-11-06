package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Point {
    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private final List<PointRecord> pointRecords = new ArrayList<>();

    @Column(name = "total_point", nullable = false)
    private int totalPoint;

    @Column(name = "available_point", nullable = false)
    private int availablePoint;

    private Point(final int totalPoint, final int availablePoint) {
        this.totalPoint = totalPoint;
        this.availablePoint = availablePoint;
    }

    public static Point init() {
        return new Point(0, 0);
    }

    public static Point of(final int totalPoint, final int availablePoint) {
        validatePointIsPositive(totalPoint);
        validatePointIsPositive(availablePoint);
        return new Point(totalPoint, availablePoint);
    }

    public void addPointRecords(final Member member, final PointType type, final int amount) {
        if (type.isIncreaseType()) {
            increasePoint(amount);
        } else {
            decreasePoint(amount);
        }

        pointRecords.add(PointRecord.addPointRecord(member, type, amount));
    }

    private void increasePoint(final int value) {
        validatePointIsPositive(value);

        totalPoint += value;
        availablePoint += value;
    }

    private void decreasePoint(final int value) {
        validatePointIsPositive(value);
        validatePointIsEnough(value);

        totalPoint -= value;
        availablePoint -= value;
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
            throw AnotherArtException.type(MemberErrorCode.POINT_CANNOT_BE_NEGATIVE);
        }
    }

    private void validatePointIsEnough(final int value) {
        if (totalPoint < value || availablePoint < value) {
            throw AnotherArtException.type(MemberErrorCode.POINT_IS_NOT_ENOUGH);
        }
    }
}

package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.point.PointRecord;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Point {
    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<PointRecord> pointRecords = new ArrayList<>();

    @Column(name = "total_point", nullable = false)
    private int totalPoint;

    @Column(name = "available_point", nullable = false)
    private int availablePoint;

    private Point(int totalPoint, int availablePoint) {
        this.totalPoint = totalPoint;
        this.availablePoint = availablePoint;
    }

    public static Point init() {
        return new Point(0, 0);
    }

    public static Point of(int totalPoint, int availablePoint) {
        validatePointIsPositive(totalPoint);
        validatePointIsPositive(availablePoint);
        return new Point(totalPoint, availablePoint);
    }

    public void addPointRecords(Member member, PointType type, int amount) {
        if (type.isIncreaseType()) {
            increasePoint(amount);
        } else {
            decreasePoint(amount);
        }

        pointRecords.add(PointRecord.addPointRecord(member, type, amount));
    }

    private void increasePoint(int value) {
        validatePointIsPositive(value);

        totalPoint += value;
        availablePoint += value;
    }

    private void decreasePoint(int value) {
        validatePointIsPositive(value);
        validatePointIsEnough(value);

        totalPoint -= value;
        availablePoint -= value;
    }

    public Point increaseAvailablePoint(int value) {
        validatePointIsPositive(value);
        return new Point(totalPoint, availablePoint + value);
    }

    public Point decreaseAvailablePoint(int value) {
        validatePointIsPositive(value);
        validatePointIsEnough(value);
        return new Point(totalPoint, availablePoint - value);
    }

    private static void validatePointIsPositive(int value) {
        if (value < 0) {
            throw AnotherArtException.type(MemberErrorCode.POINT_CANNOT_BE_NEGATIVE);
        }
    }

    private void validatePointIsEnough(int value) {
        if (totalPoint < value || availablePoint < value) {
            throw AnotherArtException.type(MemberErrorCode.POINT_IS_NOT_ENOUGH);
        }
    }
}

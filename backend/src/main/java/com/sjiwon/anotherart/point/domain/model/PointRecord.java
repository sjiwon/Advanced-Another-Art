package com.sjiwon.anotherart.point.domain.model;

import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.point.domain.model.PointRecord.Type.CHARGE;
import static com.sjiwon.anotherart.point.domain.model.PointRecord.Type.PURCHASE;
import static com.sjiwon.anotherart.point.domain.model.PointRecord.Type.REFUND;
import static com.sjiwon.anotherart.point.domain.model.PointRecord.Type.SOLD;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "member_point_record")
public class PointRecord extends BaseEntity<PointRecord> {
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Enumerated(STRING)
    @Column(name = "point_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(30)")
    private Type type;

    @Column(name = "amount", nullable = false, updatable = false)
    private int amount;

    private PointRecord(final Member member, final Type type, final int amount) {
        this.memberId = member.getId();
        this.type = type;
        this.amount = amount;
    }

    public static PointRecord addChargeRecord(final Member member, final int amount) {
        return new PointRecord(member, CHARGE, amount);
    }

    public static PointRecord addRefundRecord(final Member member, final int amount) {
        return new PointRecord(member, REFUND, amount);
    }

    public static PointRecord addArtPurchaseRecord(final Member member, final int amount) {
        return new PointRecord(member, PURCHASE, amount);
    }

    public static PointRecord addArtSoldRecord(final Member member, final int amount) {
        return new PointRecord(member, SOLD, amount);
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        CHARGE("포인트 충전"),
        REFUND("포인트 환불"),
        PURCHASE("작품 구매"),
        SOLD("작품 판매"),
        ;

        private final String description;
    }
}

package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_point_record")
public class PointRecord extends BaseEntity<PointRecord> {
    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false, updatable = false)
    private PointType type;

    @Column(name = "amount", nullable = false, updatable = false)
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    private PointRecord(final Member member, final PointType type, final int amount) {
        this.member = member;
        this.type = type;
        this.amount = amount;
    }

    public static PointRecord addPointRecord(final Member member, final PointType type, final int amount) {
        return new PointRecord(member, type, amount);
    }
}
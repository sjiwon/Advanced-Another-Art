package com.sjiwon.anotherart.point.domain.model;

import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "member_point_record")
public class PointRecord extends BaseEntity<PointRecord> {
    @Enumerated(STRING)
    @Column(name = "point_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(30)")
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

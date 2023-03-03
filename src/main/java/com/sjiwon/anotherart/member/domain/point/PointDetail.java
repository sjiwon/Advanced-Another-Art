package com.sjiwon.anotherart.member.domain.point;

import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "point_detail")
@EntityListeners(AuditingEntityListener.class)
public class PointDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false, updatable = false, length = 10)
    private PointType pointType;

    @Column(name = "amount", nullable = false, updatable = false)
    private int amount;

    @CreatedDate
    @Column(name = "record_date")
    private LocalDateTime recordDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @Builder
    private PointDetail(Member member, PointType pointType, int amount) {
        this.member = member;
        this.pointType = pointType;
        this.amount = amount;
    }

    public static PointDetail insertPointDetail(Member member, PointType pointType, int dealAmount) {
        updateMemberAvailablePoint(member, pointType, dealAmount);
        return new PointDetail(member, pointType, dealAmount);
    }

    private static void updateMemberAvailablePoint(Member member, PointType pointType, int dealAmount) {
        if (pointType.isChargeType()) {
            member.increasePoint(dealAmount);
        } else if (pointType.isRefundType()) {
            member.decreasePoint(dealAmount);
        }
    }

    public boolean isPointIncreaseType() {
        return this.pointType == PointType.CHARGE || this.pointType == PointType.SOLD;
    }
}

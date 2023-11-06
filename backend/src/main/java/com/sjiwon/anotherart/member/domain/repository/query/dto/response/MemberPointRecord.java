package com.sjiwon.anotherart.member.domain.repository.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.point.domain.model.PointType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberPointRecord {
    private final String pointType;
    private final int amount;
    private final LocalDateTime recordDate;

    @QueryProjection
    public MemberPointRecord(final PointType pointType, final int amount, final LocalDateTime recordDate) {
        this.pointType = pointType.getDescription();
        this.amount = amount;
        this.recordDate = recordDate;
    }
}

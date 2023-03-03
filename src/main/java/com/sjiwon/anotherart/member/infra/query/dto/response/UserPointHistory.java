package com.sjiwon.anotherart.member.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import com.sjiwon.anotherart.member.domain.point.PointType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserPointHistory {
    private final String pointType;
    private final int dealAmount;
    private final LocalDateTime recordDate;

    @Builder
    @QueryProjection
    public UserPointHistory(PointType pointType, int dealAmount, LocalDateTime recordDate) {
        this.pointType = pointType.getDescription();
        this.dealAmount = dealAmount;
        this.recordDate = recordDate;
    }
}

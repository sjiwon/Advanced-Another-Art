package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.MemberPointRecord;

import java.time.LocalDateTime;

public record PointRecordResponse(
        String pointType,
        int amount,
        LocalDateTime recordDate
) {
    public static PointRecordResponse from(final MemberPointRecord result) {
        return new PointRecordResponse(
                result.pointType(),
                result.amount(),
                result.recordDate()
        );
    }
}

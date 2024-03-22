package com.sjiwon.anotherart.member.domain.repository.query.response;

import java.time.LocalDateTime;

public record MemberPointRecord(
        String pointType,
        int amount,
        LocalDateTime recordDate
) {
}

package com.sjiwon.anotherart.art.domain.repository.query.response;

import com.querydsl.core.annotations.QueryProjection;

public record LikeSummary(
        Long artId,
        Long memberId
) {
    @QueryProjection
    public LikeSummary {
    }
}

package com.sjiwon.anotherart.art.domain.repository.query.response;

import com.querydsl.core.annotations.QueryProjection;

public record HashtagSummary(
        Long artId,
        String name
) {
    @QueryProjection
    public HashtagSummary {
    }
}

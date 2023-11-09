package com.sjiwon.anotherart.art.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record HashtagSummary(
        Long artId,
        String name
) {
    @QueryProjection
    public HashtagSummary {
    }
}

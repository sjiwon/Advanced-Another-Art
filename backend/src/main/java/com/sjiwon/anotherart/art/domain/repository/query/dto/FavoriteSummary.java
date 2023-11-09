package com.sjiwon.anotherart.art.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record FavoriteSummary(
        Long artId,
        Long memberId
) {
    @QueryProjection
    public FavoriteSummary {
    }
}

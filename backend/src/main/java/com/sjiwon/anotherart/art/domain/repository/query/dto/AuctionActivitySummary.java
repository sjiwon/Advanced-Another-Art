package com.sjiwon.anotherart.art.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record AuctionActivitySummary(
        Long artId,
        long bidCount
) {
    @QueryProjection
    public AuctionActivitySummary {
    }
}
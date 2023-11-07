package com.sjiwon.anotherart.art.domain.repository.query.dto;

import com.querydsl.core.annotations.QueryProjection;

public record SimpleAuction(
        Long artId,
        int bidCount
) {
    @QueryProjection
    public SimpleAuction {
    }
}

package com.sjiwon.anotherart.art.domain.repository.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record SimpleAuction(
        Long artId,
        int bidCount
) {
    @QueryProjection
    public SimpleAuction {
    }
}

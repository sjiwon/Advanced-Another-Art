package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record SimpleAuction(
        Long artId,
        int bidCount
) {
    @QueryProjection
    public SimpleAuction {}
}

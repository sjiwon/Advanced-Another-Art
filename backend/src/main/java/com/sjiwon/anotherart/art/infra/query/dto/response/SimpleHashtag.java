package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record SimpleHashtag(
        Long artId,
        String name
) {
    @QueryProjection
    public SimpleHashtag {
    }
}

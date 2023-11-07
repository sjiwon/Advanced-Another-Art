package com.sjiwon.anotherart.art.domain.repository.query.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record SimpleHashtag(
        Long artId,
        String name
) {
    @QueryProjection
    public SimpleHashtag {
    }
}

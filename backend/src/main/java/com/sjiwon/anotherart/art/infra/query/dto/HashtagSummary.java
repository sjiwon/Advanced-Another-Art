package com.sjiwon.anotherart.art.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class HashtagSummary {
    private final Long id;
    private final Long artId;
    private final String name;

    @QueryProjection
    public HashtagSummary(Long id, Long artId, String name) {
        this.id = id;
        this.artId = artId;
        this.name = name;
    }
}

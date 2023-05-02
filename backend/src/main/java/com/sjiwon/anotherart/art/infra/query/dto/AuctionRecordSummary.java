package com.sjiwon.anotherart.art.infra.query.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class AuctionRecordSummary {
    private final Long id;
    private final Long artId;
    private final Long memberId;

    @QueryProjection
    public AuctionRecordSummary(Long id, Long artId, Long memberId) {
        this.id = id;
        this.artId = artId;
        this.memberId = memberId;
    }
}

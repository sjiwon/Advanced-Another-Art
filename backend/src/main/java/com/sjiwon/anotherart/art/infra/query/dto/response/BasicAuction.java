package com.sjiwon.anotherart.art.infra.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BasicAuction {
    private final Long id;
    private final int highestBidPrice;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private int bidCount;

    public BasicAuction(final Long id, final int highestBidPrice, final LocalDateTime startDate, final LocalDateTime endDate) {
        this.id = id;
        this.highestBidPrice = highestBidPrice;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void applyBidCount(final int bidCount) {
        this.bidCount = bidCount;
    }
}

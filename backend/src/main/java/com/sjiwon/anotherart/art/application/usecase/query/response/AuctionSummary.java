package com.sjiwon.anotherart.art.application.usecase.query.response;

import java.time.LocalDateTime;

public record AuctionSummary(
        Long id,
        int highestBidPrice,
        LocalDateTime auctionStartDate,
        LocalDateTime auctionEndDate,
        int bidCount
) {
}

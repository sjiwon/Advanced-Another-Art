package com.sjiwon.anotherart.art.infra.query.dto.response;

import java.time.LocalDateTime;

public record BasicAuction(
        Long id,
        int highestBidPrice,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}

package com.sjiwon.anotherart.auction.controller.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record BidRequest(
        @NotNull(message = "입찰가는 필수입니다.")
        @Positive(message = "입찰가는 음수가 될 수 없습니다.")
        Integer bidAmount
) {
}

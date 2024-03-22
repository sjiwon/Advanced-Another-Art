package com.sjiwon.anotherart.auction.presentation.request;

import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BidRequest(
        @NotNull(message = "입찰가는 필수입니다.")
        @Positive(message = "입찰가는 음수가 될 수 없습니다.")
        Integer bidPrice
) {
    public BidCommand toCommand(
            final long memberId,
            final long auctionId
    ) {
        return new BidCommand(memberId, auctionId, bidPrice);
    }
}

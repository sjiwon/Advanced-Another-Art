package com.sjiwon.anotherart.auction.application.usecase.command;

public record BidCommand(
        Long memberId,
        Long auctionId,
        int bidPrice
) {
}

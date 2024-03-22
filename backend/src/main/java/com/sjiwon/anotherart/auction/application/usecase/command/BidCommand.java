package com.sjiwon.anotherart.auction.application.usecase.command;

public record BidCommand(
        long memberId,
        long auctionId,
        int bidPrice
) {
}

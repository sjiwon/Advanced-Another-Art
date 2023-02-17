package com.sjiwon.anotherart.art.utils;

import com.sjiwon.anotherart.art.infra.query.dto.AuctionRecordSummary;

import java.util.List;

public class BidCountCalculator {
    public static int extractAuctionBidCountByArtId(List<AuctionRecordSummary> auctionRecordSummaries, Long artId) {
        return (int) auctionRecordSummaries.stream()
                .filter(simpleAuctionHistory -> simpleAuctionHistory.getArtId().equals(artId))
                .count();
    }
}

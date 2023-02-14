package com.sjiwon.anotherart.auction.controller.utils;

import com.sjiwon.anotherart.auction.controller.dto.request.BidRequest;

public class BidRequestUtils {
    public static BidRequest createRequest(int bidAmount) {
        return BidRequest.builder()
                .bidAmount(bidAmount)
                .build();
    }
}

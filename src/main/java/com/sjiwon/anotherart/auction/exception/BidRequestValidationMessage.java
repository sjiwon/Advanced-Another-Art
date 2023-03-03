package com.sjiwon.anotherart.auction.exception;

public class BidRequestValidationMessage {
    public static class Bid {
        public static final String BID_AMOUNT = "입찰가는 필수입니다.";
        public static final String BID_AMOUNT_POSITIVE = "입찰가는 음수가 될 수 없습니다.";
    }
}

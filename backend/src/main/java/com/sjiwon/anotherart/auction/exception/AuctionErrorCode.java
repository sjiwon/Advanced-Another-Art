package com.sjiwon.anotherart.auction.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuctionErrorCode implements ErrorCode {
    AUCTION_END_DATE_MUST_BE_AFTER_START_DATE(HttpStatus.BAD_REQUEST, "AUCTION_001", "경매 종료날짜는 경매 시작날짜 이후여야 합니다."),
    INVALID_ART_TYPE(HttpStatus.BAD_REQUEST, "AUCTION_002", "경매 작품만 경매에 등록할 수 있습니다."),
    AUCTION_IS_NOT_IN_PROGRESS(HttpStatus.CONFLICT, "AUCTION_003", "경매가 진행되고 있지 않습니다."),
    ART_OWNER_CANNOT_BID(HttpStatus.CONFLICT, "AUCTION_004", "본인의 작품에 입찰을 진행할 수 없습니다."),
    HIGHEST_BIDDER_CANNOT_BID_AGAIN(HttpStatus.CONFLICT, "AUCTION_005", "현재 최고 입찰자는 연속해서 입찰할 수 없습니다."),
    BID_PRICE_IS_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "AUCTION_006", "입찰 금액이 부족합니다."),
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "AUCTION_007", "경매 정보를 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

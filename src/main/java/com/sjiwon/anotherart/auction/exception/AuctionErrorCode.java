package com.sjiwon.anotherart.auction.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuctionErrorCode implements ErrorCode {
    INVALID_AUCTION_DURATION(HttpStatus.BAD_REQUEST, "AUCTION_001", "경매 종료 날짜가 시작 날짜보다 먼저일 수 없습니다."),
    INVALID_ART_TYPE(HttpStatus.BAD_REQUEST, "AUCTION_002", "경매 작품만 경매에 등록할 수 있습니다."),
    INVALID_OWNER_BID(HttpStatus.BAD_REQUEST, "AUCTION_003", "작품 소유자는 본인 작품에 입찰할 수 없습니다."),
    INVALID_DUPLICATE_BID(HttpStatus.BAD_REQUEST, "AUCTION_004", "현재 최고 입찰자는 연속해서 입찰할 수 없습니다."),
    INVALID_BID_PRICE(HttpStatus.BAD_REQUEST, "AUCTION_005", "입찰 금액이 부족합니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

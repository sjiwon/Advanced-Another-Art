package com.sjiwon.anotherart.auction.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum AuctionExceptionCode implements BusinessExceptionCode {
    PERIOD_MUST_EXISTS(BAD_REQUEST, "AUCTION_001", "경매 날짜를 입력해주세요."),
    AUCTION_END_DATE_MUST_BE_AFTER_START_DATE(BAD_REQUEST, "AUCTION_002", "경매 종료 날짜는 경매 시작 날짜 이후여야 합니다."),
    INVALID_ART_TYPE(BAD_REQUEST, "AUCTION_003", "경매 작품만 경매에 등록할 수 있습니다."),
    HIGHEST_BIDDER_CANNOT_BID_AGAIN(CONFLICT, "AUCTION_004", "최고 입찰자는 연속해서 입찰을 진행할 수 없습니다."),
    BID_PRICE_IS_NOT_ENOUGH(BAD_REQUEST, "AUCTION_005", "입찰 금액이 부족합니다."),
    AUCTION_IS_NOT_IN_PROGRESS(CONFLICT, "AUCTION_006", "경매가 진행되고 있지 않습니다."),
    ART_OWNER_CANNOT_BID(CONFLICT, "AUCTION_007", "본인의 작품에 입찰을 진행할 수 없습니다."),
    AUCTION_NOT_FOUND(NOT_FOUND, "AUCTION_008", "경매 정보가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

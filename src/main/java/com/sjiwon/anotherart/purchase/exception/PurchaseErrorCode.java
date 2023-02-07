package com.sjiwon.anotherart.purchase.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurchaseErrorCode implements ErrorCode {
    ART_ALREADY_SOLD_OUT(HttpStatus.CONFLICT, "PURCHASE_001", "이미 판매된 작품입니다."),
    INSUFFICIENT_AVAILABLE_POINT(HttpStatus.CONFLICT, "PURCHASE_002", "작품 구매에 필요한 포인트가 부족합니다."),
    AUCTION_NOT_FINISHED(HttpStatus.CONFLICT, "PURCHASE_003", "경매가 진행중인 작품은 구매할 수 없습니다."),
    INVALID_HIGHEST_BIDDER(HttpStatus.FORBIDDEN, "PURCHASE_004", "최종 입찰자가 아닌 사용자는 구매할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

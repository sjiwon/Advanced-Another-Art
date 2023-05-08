package com.sjiwon.anotherart.purchase.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PurchaseErrorCode implements ErrorCode {
    ART_OWNER_CANNOT_PURCHASE_OWN(HttpStatus.CONFLICT, "PURCHASE_001", "본인의 작품을 구매할 수 없습니다."),
    ALREADY_SOLD(HttpStatus.CONFLICT, "PURCHASE_002", "이미 판매된 작품입니다."),
    AUCTION_NOT_FINISHED(HttpStatus.CONFLICT, "PURCHASE_003", "경매가 종료되지 않은 작품은 구매할 수 없습니다."),
    BUYER_IS_NOT_HIGHEST_BIDDER(HttpStatus.CONFLICT, "PURCHASE_004", "경매 작품은 최종 낙찰자가 아니면 구매할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

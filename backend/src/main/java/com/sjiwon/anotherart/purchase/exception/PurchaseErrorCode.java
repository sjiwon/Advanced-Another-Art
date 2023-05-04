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
    MEMBER_IS_NOT_HIGHEST_BIDDER(HttpStatus.CONFLICT, "PURCHASE_003", "낙찰자가 아닙니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

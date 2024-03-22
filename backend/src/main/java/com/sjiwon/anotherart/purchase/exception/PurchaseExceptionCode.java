package com.sjiwon.anotherart.purchase.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@Getter
@RequiredArgsConstructor
public enum PurchaseExceptionCode implements BusinessExceptionCode {
    // UseCase
    AUCTION_NOT_FINISHED(CONFLICT, "PURCHASE_001", "경매가 종료되지 않은 작품은 구매할 수 없습니다."),
    BUYER_IS_NOT_HIGHEST_BIDDER(CONFLICT, "PURCHASE_002", "경매 작품은 최종 낙찰자가 아니면 구매할 수 없습니다."),
    ART_OWNER_CANNOT_PURCHASE_OWN(CONFLICT, "PURCHASE_003", "본인 작품은 구매할 수 없습니다."),
    ALREADY_SOLD(CONFLICT, "PURCHASE_004", "이미 판매된 작품입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

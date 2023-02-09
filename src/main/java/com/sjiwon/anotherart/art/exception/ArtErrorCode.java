package com.sjiwon.anotherart.art.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArtErrorCode implements ErrorCode {
    INVALID_ART_NAME(HttpStatus.CONFLICT, "ART_001", "중복된 작품명입니다."),
    ART_NOT_FOUND(HttpStatus.NOT_FOUND, "ART_002", "작품 정보가 존재하지 않습니다."),
    INVALID_ART_DELETE_BY_ANONYMOUS(HttpStatus.FORBIDDEN, "ART_003", "작품 삭제는 작품 소유자만 가능합니다."),
    ALREADY_SALE(HttpStatus.CONFLICT, "ART_004", "이미 판매된 작품은 삭제할 수 없습니다."),
    ALREADY_BID_EXISTS(HttpStatus.CONFLICT, "ART_005", "이미 입찰이 진행된 경매 작품은 삭제할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

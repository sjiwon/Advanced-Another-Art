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
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

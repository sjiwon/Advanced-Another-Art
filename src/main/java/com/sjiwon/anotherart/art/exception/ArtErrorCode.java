package com.sjiwon.anotherart.art.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArtErrorCode implements ErrorCode {
    INVALID_ART_NAME(HttpStatus.CONFLICT, "ART_001", "중복된 작품명입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

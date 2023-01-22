package com.sjiwon.anotherart.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    NOT_SUPPORTED_URI_ERROR(HttpStatus.NOT_FOUND, "NOT_SUPPORTED_URI", "지원하지 않는 URI 요청입니다."),
    NOT_SUPPORTED_METHOD_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "NOT_SUPPORTED_METHOD", "지원하지 않는 Method 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_001", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_001", "내부 서버 오류입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

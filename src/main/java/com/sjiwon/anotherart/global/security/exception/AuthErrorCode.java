package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_AUTHENTICATION_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, "AUTH_001", "인증방식이 올바르지 않습니다."),
    INVALID_AUTHENTICATION_REQUEST_VALUE(HttpStatus.BAD_REQUEST, "AUTH_002", "아이디나 비밀번호를 정확하게 입력해주세요."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "토큰의 유효기간이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "토큰이 유효하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
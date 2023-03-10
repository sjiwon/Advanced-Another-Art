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
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "AUTH_003", "권한이 없습니다."),
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_004", "로그인이 필요합니다."),
    EXPIRED_OR_POLLUTED_TOKEN(HttpStatus.FORBIDDEN, "AUTH_005", "유효하지 않은 토큰입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

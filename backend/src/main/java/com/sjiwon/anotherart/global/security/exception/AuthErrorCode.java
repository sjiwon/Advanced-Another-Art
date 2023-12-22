package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    INVALID_AUTH_CONTENT_TYPE(BAD_REQUEST, "AUTH_001", "인증방식이 올바르지 않습니다."),
    INVALID_AUTH_DATA(UNAUTHORIZED, "AUTH_002", "아이디나 비밀번호를 정확하게 입력해주세요."),
    LOGIN_REQUIRED(FORBIDDEN, "AUTH_003", "로그인이 필요합니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH_004", "토큰이 유효하지 않습니다."),
    INVALID_PERMISSION(FORBIDDEN, "AUTH_005", "권한이 없습니다."),
    INVALID_AUTH_CODE(BAD_REQUEST, "AUTH_006", "인증번호가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

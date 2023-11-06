package com.sjiwon.anotherart.token.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum TokenErrorCode implements ErrorCode {
    INVALID_TOKEN(UNAUTHORIZED, "TOKEN_001", "토큰이 유효하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}

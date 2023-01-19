package com.sjiwon.anotherart.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private int statusCode;
    private String errorCode;
    private String message;

    private ErrorResponse(ErrorCode code) {
        this.statusCode = code.getStatus().value();
        this.errorCode = code.getStatus().getReasonPhrase();
        this.message = code.getMessage();
    }

    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }
}

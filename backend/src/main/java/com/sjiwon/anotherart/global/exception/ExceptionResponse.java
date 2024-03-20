package com.sjiwon.anotherart.global.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private String errorCode;
    private String message;

    private ExceptionResponse(final BusinessExceptionCode code) {
        this.errorCode = code.getErrorCode();
        this.message = code.getMessage();
    }

    public static ExceptionResponse from(final BusinessExceptionCode code) {
        return new ExceptionResponse(code);
    }

    public static ExceptionResponse of(final BusinessExceptionCode code, final String message) {
        return new ExceptionResponse(code.getErrorCode(), message);
    }
}

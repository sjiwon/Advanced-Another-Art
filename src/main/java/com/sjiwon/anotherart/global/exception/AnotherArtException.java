package com.sjiwon.anotherart.global.exception;

import lombok.Getter;

@Getter
public class AnotherArtException extends RuntimeException {
    private final int statusCode;
    private final String errorCode;
    private final String message;

    protected AnotherArtException(ErrorCode code) {
        super(code.getMessage());
        statusCode = code.getStatus().value();
        errorCode = code.getErrorCode();
        message = code.getMessage();
    }

    public static AnotherArtException type(ErrorCode code) {
        return new AnotherArtException(code);
    }
}

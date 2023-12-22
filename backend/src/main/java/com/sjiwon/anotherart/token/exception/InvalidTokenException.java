package com.sjiwon.anotherart.token.exception;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.ErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;

public class InvalidTokenException extends AnotherArtException {
    private static final ErrorCode code = AuthErrorCode.INVALID_TOKEN;

    public InvalidTokenException() {
        super(code);
    }
}

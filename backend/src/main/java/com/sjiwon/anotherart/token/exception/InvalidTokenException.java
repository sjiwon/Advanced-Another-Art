package com.sjiwon.anotherart.token.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;

public class InvalidTokenException extends BusinessException {
    private static final BusinessExceptionCode code = AuthErrorCode.INVALID_TOKEN;

    public InvalidTokenException() {
        super(code);
    }
}

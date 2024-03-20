package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class AuthException extends BusinessException {
    private final AuthErrorCode code;

    public AuthException(final AuthErrorCode code) {
        super(code);
        this.code = code;
    }
}

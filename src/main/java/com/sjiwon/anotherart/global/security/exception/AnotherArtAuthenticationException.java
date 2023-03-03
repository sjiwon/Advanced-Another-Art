package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AnotherArtAuthenticationException extends AuthenticationException {
    private final ErrorCode code;

    protected AnotherArtAuthenticationException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static AnotherArtAuthenticationException type(ErrorCode code) {
        return new AnotherArtAuthenticationException(code);
    }
}

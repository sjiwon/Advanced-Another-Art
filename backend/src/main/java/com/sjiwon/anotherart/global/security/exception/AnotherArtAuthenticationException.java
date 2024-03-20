package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AnotherArtAuthenticationException extends AuthenticationException {
    private final BusinessExceptionCode code;

    private AnotherArtAuthenticationException(final BusinessExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static AnotherArtAuthenticationException type(final BusinessExceptionCode code) {
        return new AnotherArtAuthenticationException(code);
    }
}

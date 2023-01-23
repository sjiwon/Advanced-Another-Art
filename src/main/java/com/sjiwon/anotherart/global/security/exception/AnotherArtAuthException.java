package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class AnotherArtAuthException extends AuthenticationException {
    private final ErrorCode code;

    protected AnotherArtAuthException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static AnotherArtAuthException type(ErrorCode code) {
        return new AnotherArtAuthException(code);
    }
}

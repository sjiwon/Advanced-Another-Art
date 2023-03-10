package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class AnotherArtAccessDeniedException extends AccessDeniedException {
    private final ErrorCode code;

    protected AnotherArtAccessDeniedException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static AnotherArtAccessDeniedException type(ErrorCode code) {
        return new AnotherArtAccessDeniedException(code);
    }
}

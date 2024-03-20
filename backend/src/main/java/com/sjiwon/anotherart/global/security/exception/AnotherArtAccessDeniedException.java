package com.sjiwon.anotherart.global.security.exception;

import com.sjiwon.anotherart.global.base.BusinessExceptionCode;
import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

@Getter
public class AnotherArtAccessDeniedException extends AccessDeniedException {
    private final BusinessExceptionCode code;

    private AnotherArtAccessDeniedException(final BusinessExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static AnotherArtAccessDeniedException type(final BusinessExceptionCode code) {
        return new AnotherArtAccessDeniedException(code);
    }
}

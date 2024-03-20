package com.sjiwon.anotherart.global.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class GlobalException extends BusinessException {
    private final GlobalExceptionCode code;

    public GlobalException(final GlobalExceptionCode code) {
        super(code);
        this.code = code;
    }
}

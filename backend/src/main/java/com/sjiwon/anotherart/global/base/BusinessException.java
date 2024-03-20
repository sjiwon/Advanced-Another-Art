package com.sjiwon.anotherart.global.base;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessExceptionCode code;

    public BusinessException(final BusinessExceptionCode code) {
        super(code.getMessage());
        this.code = code;
    }
}

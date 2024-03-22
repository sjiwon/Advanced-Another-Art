package com.sjiwon.anotherart.like.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class LikeException extends BusinessException {
    private final LikeExceptionCode code;

    public LikeException(final LikeExceptionCode code) {
        super(code);
        this.code = code;
    }
}

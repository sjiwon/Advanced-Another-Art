package com.sjiwon.anotherart.art.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class ArtException extends BusinessException {
    private final ArtExceptionCode code;

    public ArtException(final ArtExceptionCode code) {
        super(code);
        this.code = code;
    }
}

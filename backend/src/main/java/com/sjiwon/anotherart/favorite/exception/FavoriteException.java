package com.sjiwon.anotherart.favorite.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class FavoriteException extends BusinessException {
    private final FavoriteExceptionCode code;

    public FavoriteException(final FavoriteExceptionCode code) {
        super(code);
        this.code = code;
    }
}

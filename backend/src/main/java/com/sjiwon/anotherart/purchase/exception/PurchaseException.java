package com.sjiwon.anotherart.purchase.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class PurchaseException extends BusinessException {
    private final PurchaseExceptionCode code;

    public PurchaseException(final PurchaseExceptionCode code) {
        super(code);
        this.code = code;
    }
}

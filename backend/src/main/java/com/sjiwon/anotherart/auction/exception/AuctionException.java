package com.sjiwon.anotherart.auction.exception;

import com.sjiwon.anotherart.global.base.BusinessException;
import lombok.Getter;

@Getter
public class AuctionException extends BusinessException {
    private final AuctionExceptionCode code;

    public AuctionException(final AuctionExceptionCode code) {
        super(code);
        this.code = code;
    }
}

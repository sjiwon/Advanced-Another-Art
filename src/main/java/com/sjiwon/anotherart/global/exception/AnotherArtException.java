package com.sjiwon.anotherart.global.exception;

import lombok.Getter;

@Getter
public class AnotherArtException extends RuntimeException {
    private final ErrorCode code;

    public AnotherArtException(ErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }

    public static AnotherArtException type(ErrorCode code) {
        return new AnotherArtException(code);
    }
}

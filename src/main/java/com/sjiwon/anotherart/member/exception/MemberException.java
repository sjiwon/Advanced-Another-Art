package com.sjiwon.anotherart.member.exception;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.ErrorCode;

public class MemberException extends AnotherArtException {
    public MemberException(ErrorCode code) {
        super(code);
    }
}

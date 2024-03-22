package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.global.exception.GlobalExceptionCode.VALIDATION_ERROR;

@RequiredArgsConstructor
public enum MemberDuplicateResource {
    LOGIN_ID("login-id"),
    EMAIL("email"),
    NICKNAME("nickname"),
    PHONE("phone"),
    ;

    private final String value;

    public static MemberDuplicateResource from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new GlobalException(VALIDATION_ERROR));
    }
}

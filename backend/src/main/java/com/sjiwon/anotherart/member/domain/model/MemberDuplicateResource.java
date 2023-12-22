package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

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
                .filter(resource -> resource.value.equals(value))
                .findFirst()
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.INVALID_DUPLICATE_RESOURCE));
    }
}

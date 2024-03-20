package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.INVALID_DUPLICATE_RESOURCE;

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
                .orElseThrow(() -> new MemberException(INVALID_DUPLICATE_RESOURCE));
    }
}

package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.global.exception.GlobalExceptionCode.VALIDATION_ERROR;

@RequiredArgsConstructor
public enum ArtDuplicateResource {
    NAME("name"),
    ;

    private final String value;

    public static ArtDuplicateResource from(final String value) {
        return Arrays.stream(values())
                .filter(it -> it.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new GlobalException(VALIDATION_ERROR));
    }
}

package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ArtDuplicateResource {
    NAME("name"),
    ;

    private final String value;

    public static ArtDuplicateResource from(final String value) {
        return Arrays.stream(values())
                .filter(resource -> resource.value.equals(value))
                .findFirst()
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.INVALID_DUPLICATE_RESOURCE));
    }
}

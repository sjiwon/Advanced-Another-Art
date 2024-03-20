package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.INVALID_DUPLICATE_RESOURCE;

@RequiredArgsConstructor
public enum ArtDuplicateResource {
    NAME("name"),
    ;

    private final String value;

    public static ArtDuplicateResource from(final String value) {
        return Arrays.stream(values())
                .filter(resource -> resource.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new ArtException(INVALID_DUPLICATE_RESOURCE));
    }
}

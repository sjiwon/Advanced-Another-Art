package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ArtName {
    private static final int MAXIMUM_LENGTH = 20;

    @Column(name = "name", nullable = false, unique = true)
    private String value;

    private ArtName(String value) {
        this.value = value;
    }

    public static ArtName from(String value) {
        validateNameIsNotBlank(value);
        validateLengthIsInRange(value);
        return new ArtName(value);
    }

    private static void validateNameIsNotBlank(String value) {
        if (value.isBlank()) {
            throw AnotherArtException.type(ArtErrorCode.NAME_IS_BLANK);
        }
    }

    private static void validateLengthIsInRange(String value) {
        if (isLengthOutOfRange(value)) {
            throw AnotherArtException.type(ArtErrorCode.NAME_LENGTH_OUT_OF_RANGE);
        }
    }

    private static boolean isLengthOutOfRange(String name) {
        return MAXIMUM_LENGTH < name.length();
    }
}

package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Description {
    private static final int MAXIMUM_LENGTH = 1000;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String value;

    private Description(String value) {
        this.value = value;
    }

    public static Description from(String value) {
        validateDescriptionIsNotBlank(value);
        validateLengthIsInRange(value);
        return new Description(value);
    }

    private static void validateDescriptionIsNotBlank(String value) {
        if (value.isBlank()) {
            throw AnotherArtException.type(ArtErrorCode.DESCRIPTION_IS_BLANK);
        }
    }

    private static void validateLengthIsInRange(String value) {
        if (isLengthOutOfRange(value)) {
            throw AnotherArtException.type(ArtErrorCode.DESCRIPTION_LENGTH_OUT_OF_RANGE);
        }
    }

    private static boolean isLengthOutOfRange(String name) {
        return MAXIMUM_LENGTH < name.length();
    }
}

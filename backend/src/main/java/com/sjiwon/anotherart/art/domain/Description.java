package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Description {
    private static final int MAXIMUM_LENGTH = 1000;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String value;

    private Description(final String value) {
        this.value = value;
    }

    public static Description from(final String value) {
        validateDescriptionIsNotBlank(value);
        validateLengthIsInRange(value);
        return new Description(value);
    }

    private static void validateDescriptionIsNotBlank(final String value) {
        if (value.isBlank()) {
            throw AnotherArtException.type(ArtErrorCode.DESCRIPTION_IS_BLANK);
        }
    }

    private static void validateLengthIsInRange(final String value) {
        if (isLengthOutOfRange(value)) {
            throw AnotherArtException.type(ArtErrorCode.DESCRIPTION_LENGTH_OUT_OF_RANGE);
        }
    }

    private static boolean isLengthOutOfRange(final String name) {
        return MAXIMUM_LENGTH < name.length();
    }
}

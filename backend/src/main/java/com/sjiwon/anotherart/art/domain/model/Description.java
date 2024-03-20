package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.DESCRIPTION_IS_BLANK;
import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.DESCRIPTION_LENGTH_OUT_OF_RANGE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
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
        validateDescriptionIsUsable(value);
        validateLengthIsInRange(value);
        return new Description(value);
    }

    private static void validateDescriptionIsUsable(final String value) {
        if (value == null || value.isBlank()) {
            throw new ArtException(DESCRIPTION_IS_BLANK);
        }
    }

    private static void validateLengthIsInRange(final String value) {
        if (isLengthOutOfRange(value)) {
            throw new ArtException(DESCRIPTION_LENGTH_OUT_OF_RANGE);
        }
    }

    private static boolean isLengthOutOfRange(final String name) {
        return name.length() > MAXIMUM_LENGTH;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final Description other = (Description) object;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

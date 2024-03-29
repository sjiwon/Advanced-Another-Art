package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.NAME_IS_BLANK;
import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.NAME_LENGTH_OUT_OF_RANGE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class ArtName {
    private static final int MAXIMUM_LENGTH = 20;

    @Column(name = "name", nullable = false, unique = true)
    private String value;

    private ArtName(final String value) {
        this.value = value;
    }

    public static ArtName from(final String value) {
        validateNameIsUsable(value);
        validateLengthIsInRange(value);
        return new ArtName(value);
    }

    private static void validateNameIsUsable(final String value) {
        if (value == null || value.isBlank()) {
            throw new ArtException(NAME_IS_BLANK);
        }
    }

    private static void validateLengthIsInRange(final String value) {
        if (value.length() > MAXIMUM_LENGTH) {
            throw new ArtException(NAME_LENGTH_OUT_OF_RANGE);
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final ArtName other = (ArtName) object;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

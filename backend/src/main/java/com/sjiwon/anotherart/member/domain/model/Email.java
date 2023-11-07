package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String value;

    private Email(final String value) {
        this.value = value;
    }

    public static Email from(final String value) {
        validateEmailPattern(value);
        return new Email(value);
    }

    private static void validateEmailPattern(final String value) {
        if (isNotValidPattern(value)) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_EMAIL_PATTERN);
        }
    }

    private static boolean isNotValidPattern(final String email) {
        return !EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        final Email other = (Email) object;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

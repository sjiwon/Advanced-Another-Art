package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.INVALID_PHONE_PATTERN;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Phone {
    /**
     * xxx-xxx-xxxx or xxx-xxxx-xxxx
     */
    private static final Pattern pattern = Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$");

    @Column(name = "phone", nullable = false, unique = true)
    private String value;

    private Phone(final String value) {
        this.value = value;
    }

    public static Phone from(final String value) {
        validatePhonePattern(value);
        return new Phone(value);
    }

    private static void validatePhonePattern(final String value) {
        if (isNotValidPattern(value)) {
            throw new MemberException(INVALID_PHONE_PATTERN);
        }
    }

    private static boolean isNotValidPattern(final String value) {
        return !pattern.matcher(value).matches();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Phone other = (Phone) o;

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

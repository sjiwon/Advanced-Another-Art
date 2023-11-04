package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Password {
    /**
     * 영문 + 숫자 + 특수기호 + 8자 이상 25자 이하
     */
    private static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,25}$";
    private static final Pattern PASSWORD_MATCHER = Pattern.compile(PASSWORD_PATTERN);

    @Column(name = "password", nullable = false, length = 200)
    private String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password encrypt(final String value, final PasswordEncoder encoder) {
        validatePasswordPattern(value);
        return new Password(encoder.encode(value));
    }

    public Password update(final String value, final PasswordEncoder encoder) {
        validatePasswordPattern(value);
        validatePasswordSameAsBefore(value, encoder);
        return new Password(encoder.encode(value));
    }

    private static void validatePasswordPattern(final String value) {
        if (isNotValidPattern(value)) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_PASSWORD_PATTERN);
        }
    }

    private void validatePasswordSameAsBefore(final String value, final PasswordEncoder encoder) {
        if (encoder.matches(value, this.value)) {
            throw AnotherArtException.type(MemberErrorCode.PASSWORD_SAME_AS_BEFORE);
        }
    }

    private static boolean isNotValidPattern(final String password) {
        return !PASSWORD_MATCHER.matcher(password).matches();
    }
}

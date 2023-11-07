package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
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
public class Password {
    /**
     * 영문 + 숫자 + 특수기호 + 8자 이상 25자 이하
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,25}$");

    @Column(name = "password", nullable = false, length = 200)
    private String value;

    private Password(final String value) {
        this.value = value;
    }

    public static Password encrypt(final String value, final PasswordEncryptor encryptor) {
        validatePasswordPattern(value);
        return new Password(encryptor.encode(value));
    }

    public Password update(final String value, final PasswordEncryptor encryptor) {
        validatePasswordPattern(value);
        validatePasswordSameAsBefore(value, encryptor);
        return new Password(encryptor.encode(value));
    }

    private static void validatePasswordPattern(final String value) {
        if (isInvalidPattern(value)) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_PASSWORD_PATTERN);
        }
    }

    private static boolean isInvalidPattern(final String password) {
        return !PASSWORD_PATTERN.matcher(password).matches();
    }

    private void validatePasswordSameAsBefore(final String value, final PasswordEncryptor encryptor) {
        if (encryptor.matches(value, this.value)) {
            throw AnotherArtException.type(MemberErrorCode.PASSWORD_SAME_AS_BEFORE);
        }
    }
}

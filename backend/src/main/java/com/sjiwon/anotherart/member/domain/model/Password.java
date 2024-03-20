package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.INVALID_PASSWORD_PATTERN;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.PASSWORD_SAME_AS_BEFORE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
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

    public static Password encrypt(final String value, final Encryptor encryptor) {
        validatePasswordPattern(value);
        return new Password(encryptor.hash(value));
    }

    public Password update(final String value, final Encryptor encryptor) {
        validatePasswordPattern(value);
        validatePasswordSameAsBefore(value, encryptor);
        return new Password(encryptor.hash(value));
    }

    private static void validatePasswordPattern(final String value) {
        if (isInvalidPattern(value)) {
            throw new MemberException(INVALID_PASSWORD_PATTERN);
        }
    }

    private static boolean isInvalidPattern(final String password) {
        return !PASSWORD_PATTERN.matcher(password).matches();
    }

    private void validatePasswordSameAsBefore(final String value, final Encryptor encryptor) {
        if (encryptor.matches(value, this.value)) {
            throw new MemberException(PASSWORD_SAME_AS_BEFORE);
        }
    }
}

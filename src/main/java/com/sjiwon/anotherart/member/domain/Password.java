package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Password {
    /**
     * 1. 적어도 8자리 이상
     * 2. 하나 이상의 숫자 포함
     * 3. 하나 이상의 알파벳 소문자 포함
     * 4. 하나 이상의 알파벳 대문자 포함
     * 5. 하나 이상의 특수문자 포함 (!, @, #)
     */
    private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#])[\\da-zA-Z!@#]{8,}$";
    private static final Pattern PASSWORD_MATCHER = Pattern.compile(PASSWORD_PATTERN);

    @Column(name = "password", nullable = false, length = 200)
    private String value;

    private Password(String value) {
        this.value = value;
    }

    public static Password encrypt(String value, PasswordEncoder encoder) {
        validatePasswordPattern(value);
        return new Password(encoder.encode(value));
    }

    public Password update(String value, PasswordEncoder encoder) {
        validatePasswordPattern(value);
        return new Password(encoder.encode(value));
    }

    private static void validatePasswordPattern(String value) {
        if (isNotValidPattern(value)) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_PASSWORD_PATTERN);
        }
    }

    private static boolean isNotValidPattern(String password) {
        return !PASSWORD_MATCHER.matcher(password).matches();
    }
}

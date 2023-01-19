package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.member.exception.MemberException;
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
    // 글자 제한 -> 8 ~ 30
    // 알파벳 대소문자, 숫자, 특수문자를 각각 1개 이상 포함
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{8,30}$";
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
            throw MemberException.type(MemberErrorCode.INVALID_PASSWORD_PATTERN);
        }
    }

    private static boolean isNotValidPattern(String password) {
        return !PASSWORD_MATCHER.matcher(password).matches();
    }
}

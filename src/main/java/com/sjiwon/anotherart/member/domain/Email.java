package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
    private static final Pattern EMAIL_MATCHER = Pattern.compile(EMAIL_PATTERN);

    @Column(name = "email", nullable = false, unique = true, updatable = false, length = 100)
    private String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email from(String value) {
        validateEmailPattern(value);
        return new Email(value);
    }

    private static void validateEmailPattern(String value) {
        if (isNotValidPattern(value)) {
            throw MemberException.type(MemberErrorCode.INVALID_EMAIL_PATTERN);
        }
    }

    private static boolean isNotValidPattern(String password) {
        return !EMAIL_MATCHER.matcher(password).matches();
    }
}

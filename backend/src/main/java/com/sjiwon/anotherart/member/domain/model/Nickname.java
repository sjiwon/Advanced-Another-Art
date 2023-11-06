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
public class Nickname {
    /**
     * 1. 한글 & 알파벳 대소문자 & 숫자 가능 <br>
     * 2. 공백 불가능 <br>
     * 3. 2자 이상 10자 이하
     */
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-zA-Z가-힣0-9]{2,10}$");

    @Column(name = "nickname", nullable = false, unique = true)
    private String value;

    private Nickname(final String value) {
        this.value = value;
    }

    public static Nickname from(final String value) {
        validateNicknamePattern(value);
        return new Nickname(value);
    }

    public Nickname update(final String value) {
        validateNicknamePattern(value);
        validateNicknameSameAsBefore(value);
        return new Nickname(value);
    }

    private static void validateNicknamePattern(final String value) {
        if (isInvalidPattern(value)) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_NICKNAME_FORMAT);
        }
    }

    private void validateNicknameSameAsBefore(final String value) {
        if (this.value.equals(value)) {
            throw AnotherArtException.type(MemberErrorCode.NICKNAME_SAME_AS_BEFORE);
        }
    }

    private static boolean isInvalidPattern(final String nickname) {
        return !NICKNAME_MATCHER.matcher(nickname).matches();
    }
}

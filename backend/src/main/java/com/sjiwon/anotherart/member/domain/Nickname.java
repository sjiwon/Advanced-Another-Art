package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Nickname {
    // 한글 & 알파벳 대소문자 & 숫자 가능
    // 공백 불가능
    // 2자 이상 10자 이하
    private static final String NICKNAME_PATTERN = "^[a-zA-Z가-힣0-9]{2,10}$";
    private static final Pattern NICKNAME_MATCHER = Pattern.compile(NICKNAME_PATTERN);

    @Column(name = "nickname", nullable = false, unique = true)
    private String value;

    private Nickname(String value) {
        this.value = value;
    }

    public static Nickname from(String value) {
        validateNicknamePattern(value);
        return new Nickname(value);
    }

    public Nickname update(String value) {
        validateNicknamePattern(value);
        validateNicknameSameAsBefore(value);
        return new Nickname(value);
    }

    private static void validateNicknamePattern(String value) {
        if (isInvalidPattern(value)) {
            throw AnotherArtException.type(MemberErrorCode.INVALID_NICKNAME_FORMAT);
        }
    }

    private void validateNicknameSameAsBefore(String value) {
        if (this.value.equals(value)) {
            throw AnotherArtException.type(MemberErrorCode.NICKNAME_SAME_AS_BEFORE);
        }
    }

    private static boolean isInvalidPattern(String nickname) {
        return !NICKNAME_MATCHER.matcher(nickname).matches();
    }
}
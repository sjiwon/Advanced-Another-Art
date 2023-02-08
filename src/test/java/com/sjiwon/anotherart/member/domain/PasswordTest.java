package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 도메인 {Password VO} 테스트")
class PasswordTest {
    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "aaabbbcccdddeeeAAABBBCCCDDDEEE123123123!@#$@!#%!@%!@#$!@#"})
    @DisplayName("형식에 맞지 않는 패스워드면 예외가 발생한다")
    void test1(String value){
        assertThatThrownBy(() -> Password.encrypt(value, ENCODER))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.INVALID_PASSWORD_PATTERN.getMessage());
    }
    
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"ABCabc!@#123", "DJKAqwe!@#a1"})
    @DisplayName("패스워드 업데이트를 성공적으로 수행한다")
    void test2(String value){
        // given
        Password password = Password.encrypt("abcABC123!@#", ENCODER);

        // when
        Password updatePassword = password.update(value, ENCODER);

        // then
        assertThat(ENCODER.matches(value, updatePassword.getValue())).isTrue();
    }

    @Test
    @DisplayName("이전과 동일한 비밀번호인지 검증한다")
    void test3() {
        // given
        Password password = Password.encrypt("abcABC123!@#", ENCODER);
        String comparePassword1 = "abcABC123!@#";
        String comparePassword2 = "abcABC1234!@#";

        // when
        boolean actual1 = password.isSamePassword(comparePassword1, ENCODER);
        boolean actual2 = password.isSamePassword(comparePassword2, ENCODER);

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }
}
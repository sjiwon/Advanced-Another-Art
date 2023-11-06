package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 {Password VO} 테스트")
class PasswordTest {
    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "aaabbbcccdddeeeAAABBBCCCDDDEEE123123123!@#$@!#%!@%!@#$!@#"})
    @DisplayName("형식에 맞지 않는 패스워드면 예외가 발생한다")
    void throwExceptionByInalidPasswordPattern(final String value) {
        assertThatThrownBy(() -> Password.encrypt(value, ENCODER))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.INVALID_PASSWORD_PATTERN.getMessage());
    }

    @Test
    @DisplayName("Password를 생성한다")
    void construct() {
        final Password password = Password.encrypt("abcABC123!@#", ENCODER);

        assertAll(
                () -> assertThat(password.getValue()).isNotEqualTo("abcABC123!@#"),
                () -> assertThat(ENCODER.matches("abcABC123!@#", password.getValue())).isTrue()
        );
    }

    @Nested
    @DisplayName("Password 수정")
    class update {
        @Test
        @DisplayName("이전과 동일한 Password로 수정하려고 하면 예외가 발생한다")
        void throwExceptionByPasswordSameAsBefore() {
            // given
            final Password password = Password.encrypt("abcABC123!@#", ENCODER);

            // when - then
            Assertions.assertThatThrownBy(() -> password.update("abcABC123!@#", ENCODER))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("Password 수정에 성공한다")
        void success() {
            // given
            final Password password = Password.encrypt("abcABC123!@#", ENCODER);

            // when
            final Password updatePassword = password.update("abcABC123!@#123", ENCODER);

            // then
            assertAll(
                    () -> assertThat(ENCODER.matches("abcABC123!@#", updatePassword.getValue())).isFalse(),
                    () -> assertThat(ENCODER.matches("abcABC123!@#123", updatePassword.getValue())).isTrue()
            );
        }
    }
}

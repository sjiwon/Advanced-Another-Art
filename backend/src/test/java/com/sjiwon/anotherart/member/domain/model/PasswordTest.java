package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.mock.fake.FakePasswordEncryptor;
import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Password VO] 테스트")
class PasswordTest {
    private final PasswordEncryptor passwordEncryptor = new FakePasswordEncryptor();

    @Nested
    @DisplayName("Password 생성")
    class Construct {
        @ParameterizedTest
        @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "aaabbbcccdddeeeAAABBBCCCDDDEEE123123123!@#$@!#%!@%!@#$!@#"})
        @DisplayName("형식에 맞지 않는 패스워드면 예외가 발생한다")
        void throwExceptionByInalidPasswordPattern(final String value) {
            assertThatThrownBy(() -> Password.encrypt(value, passwordEncryptor))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.INVALID_PASSWORD_PATTERN.getMessage());
        }

        @Test
        @DisplayName("Password를 생성한다")
        void success() {
            // given
            final String rawValue = "abcABC123!@#";

            // when
            final Password password = Password.encrypt(rawValue, passwordEncryptor);

            // then
            assertAll(
                    () -> assertThat(password.getValue()).isNotEqualTo("abcABC123!@#"),
                    () -> assertThat(passwordEncryptor.matches(rawValue, password.getValue())).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("Password 수정")
    class update {
        @Test
        @DisplayName("이전과 동일한 Password로 수정하려고 하면 예외가 발생한다")
        void throwExceptionByPasswordSameAsBefore() {
            // given
            final String oldValue = "abcABC123!@#";
            final Password password = Password.encrypt(oldValue, passwordEncryptor);

            // when - then
            Assertions.assertThatThrownBy(() -> password.update(oldValue, passwordEncryptor))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.PASSWORD_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("Password를 수정한다")
        void success() {
            // given
            final String oldValue = "abcABC123!@#";
            final Password oldPassword = Password.encrypt(oldValue, passwordEncryptor);

            // when
            final String newValue = "abcABC123!@#123";
            final Password newPassword = oldPassword.update(newValue, passwordEncryptor);

            // then
            assertAll(
                    () -> assertThat(passwordEncryptor.matches(oldValue, newPassword.getValue())).isFalse(),
                    () -> assertThat(passwordEncryptor.matches(newValue, newPassword.getValue())).isTrue()
            );
        }
    }
}

package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.mock.fake.FakeEncryptor;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
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
    private final Encryptor encryptor = new FakeEncryptor();

    @Nested
    @DisplayName("Password 생성")
    class Construct {
        @ParameterizedTest
        @ValueSource(strings = {"", "123", "abc", "!@#", "Tabc12!", "aaabbbcccdddeeeAAABBBCCCDDDEEE123123123!@#$@!#%!@%!@#$!@#"})
        @DisplayName("형식에 맞지 않는 패스워드면 예외가 발생한다")
        void throwExceptionByInalidPasswordPattern(final String value) {
            assertThatThrownBy(() -> Password.encrypt(value, encryptor))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.INVALID_PASSWORD_PATTERN.getMessage());
        }

        @Test
        @DisplayName("Password를 생성한다")
        void success() {
            // given
            final String rawValue = "abcABC123!@#";

            // when
            final Password password = Password.encrypt(rawValue, encryptor);

            // then
            assertAll(
                    () -> assertThat(password.getValue()).isNotEqualTo("abcABC123!@#"),
                    () -> assertThat(encryptor.matches(rawValue, password.getValue())).isTrue()
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
            final Password password = Password.encrypt(oldValue, encryptor);

            // when - then
            Assertions.assertThatThrownBy(() -> password.update(oldValue, encryptor))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.PASSWORD_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("Password를 수정한다")
        void success() {
            // given
            final String oldValue = "abcABC123!@#";
            final Password oldPassword = Password.encrypt(oldValue, encryptor);

            // when
            final String newValue = "abcABC123!@#123";
            final Password newPassword = oldPassword.update(newValue, encryptor);

            // then
            assertAll(
                    () -> assertThat(encryptor.matches(oldValue, newPassword.getValue())).isFalse(),
                    () -> assertThat(encryptor.matches(newValue, newPassword.getValue())).isTrue()
            );
        }
    }
}

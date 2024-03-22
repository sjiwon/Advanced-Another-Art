package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member -> 도메인 [Email] 테스트")
class EmailTest extends UnitTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@gmail."})
    @DisplayName("형식에 맞지 않는 Email이면 예외가 발생한다")
    void throwExceptionByInvalidEmailPattern(final String value) {
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExceptionCode.INVALID_EMAIL_PATTERN.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"sjiwon4491@gmail.com", "sjiwon4491@naver.com", "sjiwon@kyonggi.ac.kr"})
    @DisplayName("Email을 생성한다")
    void construct(final String value) {
        final Email email = Email.from(value);

        assertThat(email.getValue()).isEqualTo(value);
    }
}

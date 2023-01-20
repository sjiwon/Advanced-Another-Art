package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 도메인 {Email VO} 테스트")
class EmailTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(strings = {"", "abc", "@gmail.com", "abc@gmail", "abc@gmail."})
    @DisplayName("형식에 맞지 않는 이메일이면 예외가 발생한다")
    void test(String value){
        assertThatThrownBy(() -> Email.from(value))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.INVALID_EMAIL_PATTERN.getMessage());
    }
}
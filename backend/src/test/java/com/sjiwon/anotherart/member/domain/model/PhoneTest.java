package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member -> 도메인 [Phone] 테스트")
class PhoneTest extends UnitTest {
    @ParameterizedTest
    @ValueSource(strings = {"01012345678", "010-12345678", "0101234-5678", "010-12-3456", "010-123-456", "01-234-5678"})
    @DisplayName("형식에 맞지 않는 Phone이면 생성에 실패한다")
    void throwExceptionByInvalidPhoneFormat(final String value) {
        assertThatThrownBy(() -> Phone.from(value))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberExceptionCode.INVALID_PHONE_PATTERN.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"010-1234-5678", "010-123-4567"})
    @DisplayName("Phone을 생성한다")
    void construct(final String value) {
        assertDoesNotThrow(() -> Phone.from(value));
    }
}

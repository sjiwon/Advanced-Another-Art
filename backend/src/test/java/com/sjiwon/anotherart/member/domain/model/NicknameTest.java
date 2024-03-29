package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member -> 도메인 [Nickname] 테스트")
class NicknameTest extends UnitTest {
    @Nested
    @DisplayName("Nickname 생성")
    class Construct {
        @ParameterizedTest
        @ValueSource(strings = {"한", "!@#hello", "Hello World", "일이삼사오육칠팔구십십일"})
        @DisplayName("형식에 맞지 않는 Nickname이면 생성에 실패한다")
        void throwExceptionByInvalidNicknameFormat(final String value) {
            assertThatThrownBy(() -> Nickname.from(value))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.INVALID_NICKNAME_FORMAT.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"하이", "하이123", "hEllo123"})
        @DisplayName("Nickname을 생성한다")
        void success(final String value) {
            final Nickname nickname = Nickname.from(value);

            assertThat(nickname.getValue()).isEqualTo(value);
        }
    }

    @Nested
    @DisplayName("Nickname 수정")
    class Update {
        @Test
        @DisplayName("이전과 동일한 Nickname으로 수정하려고 하면 예외가 발생한다")
        void throwExceptionByNicknameSameAsBefore() {
            // given
            final Nickname nickname = Nickname.from("Hello");

            // when - then
            assertThatThrownBy(() -> nickname.update("Hello"))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.NICKNAME_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("Nickname 수정에 성공한다")
        void success() {
            // given
            final Nickname nickname = Nickname.from("Hello");

            // when
            final Nickname updateNickname = nickname.update("Hello2");

            // then
            assertThat(updateNickname.getValue()).isEqualTo("Hello2");
        }
    }
}

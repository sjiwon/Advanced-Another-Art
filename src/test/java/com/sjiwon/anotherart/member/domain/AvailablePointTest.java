package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member 도메인 {AvailablePoint VO} 테스트")
class AvailablePointTest {
    @ParameterizedTest(name = "{index}: {0}")
    @ValueSource(ints = {-1, -2, -3, -4, -5})
    @DisplayName("사용자의 사용 가능한 포인트는 반드시 0보다 커야한다")
    void test1(int value) {
        assertThatThrownBy(() -> AvailablePoint.from(value))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.INVALID_AVAILABLE_POINTS.getMessage());
    }

    @ParameterizedTest(name = "{index}: {0} 증가")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    @DisplayName("사용자의 사용 가능한 포인트를 성공적으로 증가시킨다")
    void test2(int value) {
        // given
        final int DEFAULT_POINT = 10;
        AvailablePoint point = AvailablePoint.from(DEFAULT_POINT);

        // when
        AvailablePoint increasePoint = point.increasePoint(value);

        // when
        assertThat(increasePoint.getValue()).isEqualTo(DEFAULT_POINT + value);
    }

    @ParameterizedTest(name = "{index}: {0} 감소")
    @ValueSource(ints = {11, 12, 13})
    @DisplayName("현재 보유한 포인트에 비해 큰 포인트를 감소시킴에 따라 예외가 발생한다")
    void test3(int value){
        // given
        final int DEFAULT_POINT = 10;
        AvailablePoint point = AvailablePoint.from(DEFAULT_POINT);

        // when
        assertThatThrownBy(() -> point.decreasePoint(value))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.INVALID_POINT_DECREASE.getMessage());
    }
    
    @ParameterizedTest(name = "{index}: {0} 감소")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    @DisplayName("사용자의 사용 가능한 포인트를 성공적으로 감소시킨다")
    void test4(int value) {
        // given
        final int DEFAULT_POINT = 10;
        AvailablePoint point = AvailablePoint.from(DEFAULT_POINT);

        // when
        AvailablePoint decreasePoint = point.decreasePoint(value);

        // when
        assertThat(decreasePoint.getValue()).isEqualTo(DEFAULT_POINT - value);
    }
}
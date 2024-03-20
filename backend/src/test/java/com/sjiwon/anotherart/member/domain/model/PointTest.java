package com.sjiwon.anotherart.member.domain.model;

import com.sjiwon.anotherart.member.exception.MemberException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member -> 도메인 [Point VO] 테스트")
class PointTest {
    @Nested
    @DisplayName("전체 포인트 증가 [포인트 충전 / 작품 판매]")
    class IncreaseTotalPoint {
        @Test
        @DisplayName("전체 포인트를 증가시킨다")
        void success() {
            // given
            final Point point = Point.init()
                    .increaseTotalPoint(100_000)
                    .decreaseAvailablePoint(5_000);

            assertAll(
                    () -> assertThat(point.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000 - 5_000)
            );

            // when
            final Point increasePoint = point.increaseTotalPoint(50_000);

            // then
            assertAll(
                    () -> assertThat(increasePoint.getTotalPoint()).isEqualTo(100_000 + 50_000),
                    () -> assertThat(increasePoint.getAvailablePoint()).isEqualTo(100_000 - 5_000 + 50_000)
            );
        }
    }

    @Nested
    @DisplayName("전체 포인트 감소 [포인트 환불 / 작품 구매]")
    class DecreaseTotalPoint {
        @Test
        @DisplayName("사용 가능한 포인트가 충분하지 않음에 따라 감소시킬 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            final Point point = Point.init()
                    .increaseTotalPoint(100_000);

            // when - then
            assertThatThrownBy(() -> point.decreaseTotalPoint(100_000 + 5_000))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("전체 포인트를 감소시킨다")
        void success() {
            // given
            final Point point = Point.init()
                    .increaseTotalPoint(100_000);

            // when
            final Point decreasePoint = point.decreaseTotalPoint(99_999);

            // then
            assertAll(
                    () -> assertThat(decreasePoint.getTotalPoint()).isEqualTo(100_000 - 99_999),
                    () -> assertThat(decreasePoint.getAvailablePoint()).isEqualTo(100_000 - 99_999)
            );
        }
    }

    @Nested
    @DisplayName("사용 가능한 포인트 증가 [경매 작품 최고 입찰자에서 물러났을 경우]")
    class IncreaseAvailablePoint {
        @Test
        @DisplayName("사용 가능한 포인트를 증가시킨다")
        void success() {
            // given
            final Point point = Point.init()
                    .increaseTotalPoint(100_000)
                    .decreaseAvailablePoint(5_000);

            assertAll(
                    () -> assertThat(point.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000 - 5_000)
            );

            // when
            final Point increasePoint = point.increaseAvailablePoint(5_000);

            // then
            assertAll(
                    () -> assertThat(increasePoint.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(increasePoint.getAvailablePoint()).isEqualTo(100_000 - 5_000 + 5_000)
            );
        }
    }

    @Nested
    @DisplayName("사용 가능한 포인트 감소 [경매 작품 입찰 참여]")
    class DecreaseAvailablePoint {
        @Test
        @DisplayName("사용 가능한 포인트가 충분하지 않음에 따라 감소시킬 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            final Point point = Point.init()
                    .increaseTotalPoint(100_000);

            // when - then
            assertThatThrownBy(() -> point.decreaseAvailablePoint(100_000 + 5_000))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("사용 가능한 포인트를 감소시킨다")
        void success() {
            // given
            final Point point = Point.init()
                    .increaseTotalPoint(100_000);

            // when
            final Point decreasePoint = point.decreaseAvailablePoint(99_999);

            // then
            assertAll(
                    () -> assertThat(decreasePoint.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(decreasePoint.getAvailablePoint()).isEqualTo(100_000 - 99_999)
            );
        }
    }
}

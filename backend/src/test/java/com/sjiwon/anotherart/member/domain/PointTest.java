package com.sjiwon.anotherart.member.domain;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.point.PointRecord;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 도메인 {Point VO} 테스트")
class PointTest {
    private Member member;

    @BeforeEach
    void setUp() {
        member = MEMBER_A.toMember();
    }

    @Test
    @DisplayName("Point를 생성한다")
    void construct() {
        Point init = Point.init();
        Point point = Point.of(100_000, 100_000);

        assertAll(
                () -> assertThat(init.getPointRecords()).hasSize(0),
                () -> assertThat(init.getTotalPoint()).isEqualTo(0),
                () -> assertThat(init.getAvailablePoint()).isEqualTo(0),

                () -> assertThat(point.getPointRecords()).hasSize(0),
                () -> assertThat(point.getTotalPoint()).isEqualTo(100_000),
                () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000)
        );

        assertThatThrownBy(() -> Point.of(-1, -1))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.POINT_CANNOT_BE_NEGATIVE.getMessage());
    }

    @Test
    @DisplayName("포인트 활용 내역을 기록한다")
    void addPointRecords() {
        // given
        Point point = Point.init();

        /* 포인트 충전 */
        point.addPointRecords(member, CHARGE, 100_000);
        assertAll(
                () -> assertThat(point.getPointRecords()).hasSize(1),
                () -> assertThat(point.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE),
                () -> assertThat(point.getTotalPoint()).isEqualTo(100_000),
                () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000)
        );

        /* 포인트 환불 */
        point.addPointRecords(member, REFUND, 50_000);
        assertAll(
                () -> assertThat(point.getPointRecords()).hasSize(2),
                () -> assertThat(point.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE, REFUND),
                () -> assertThat(point.getTotalPoint()).isEqualTo(100_000 - 50_000),
                () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000 - 50_000)
        );

        /* 작품 구매 */
        point.addPointRecords(member, PURCHASE, 30_000);
        assertAll(
                () -> assertThat(point.getPointRecords()).hasSize(3),
                () -> assertThat(point.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE, REFUND, PURCHASE),
                () -> assertThat(point.getTotalPoint()).isEqualTo(100_000 - 50_000 - 30_000),
                () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000 - 50_000 - 30_000)
        );

        /* 작품 판매 */
        point.addPointRecords(member, SOLD, 100_000);
        assertAll(
                () -> assertThat(point.getPointRecords()).hasSize(4),
                () -> assertThat(point.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(CHARGE, REFUND, PURCHASE, SOLD),
                () -> assertThat(point.getTotalPoint()).isEqualTo(100_000 - 50_000 - 30_000 + 100_000),
                () -> assertThat(point.getAvailablePoint()).isEqualTo(100_000 - 50_000 - 30_000 + 100_000)
        );
    }

    @Test
    @DisplayName("사용 가능한 포인트를 증가시킨다 [경매 작품 최고 입찰자에서 물러났을 경우]")
    void increaseAvailablePoint() {
        // given
        Point point = Point.of(100_000, 0);

        // when
        Point increasePoint = point.increaseAvailablePoint(50_000);

        // then
        assertAll(
                () -> assertThat(increasePoint.getTotalPoint()).isEqualTo(100_000),
                () -> assertThat(increasePoint.getAvailablePoint()).isEqualTo(50_000)
        );
    }

    @Nested
    @DisplayName("사용 가능한 포인트 감소")
    class decreaseAvailablePoint {
        @Test
        @DisplayName("사용 가능한 포인트가 충분하지 않음에 따라 감소시킬 수 없다")
        void throwExceptionByPointIsNotEnough() {
            // given
            Point point = Point.of(100_000, 0);

            
            // when - then
            assertThatThrownBy(() -> point.decreaseAvailablePoint(50_000))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }
        
        @Test
        @DisplayName("사용 가능한 포인트를 감소시킨다 [경매 작품 입찰]")
        void success() {
            // given
            Point point = Point.of(100_000, 100_000);
            
            // when
            Point decreasePoint = point.decreaseAvailablePoint(99_999);

            // then
            assertAll(
                    () -> assertThat(decreasePoint.getTotalPoint()).isEqualTo(100_000),
                    () -> assertThat(decreasePoint.getAvailablePoint()).isEqualTo(1)
            );
        }
    }
}

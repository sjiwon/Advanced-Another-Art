package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointRecord;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static com.sjiwon.anotherart.member.domain.point.PointType.REFUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberPointService 테스트")
class MemberPointServiceTest extends ServiceTest {
    @Autowired
    private MemberPointService memberPointService;

    private static final int CHARGE_AMOUNT = 100_000;
    private static final int REFUND_AMOUNT = 50_000;
    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());
    }

    @Test
    @DisplayName("포인트를 충전한다")
    void chargePoint() {
        // when
        memberPointService.chargePoint(member.getId(), CHARGE_AMOUNT);

        // then
        final Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        assertAll(
                () -> assertThat(findMember.getPointRecords()).hasSize(1),
                () -> assertThat(findMember.getPointRecords())
                        .map(PointRecord::getType)
                        .containsExactly(CHARGE),
                () -> assertThat(findMember.getPointRecords())
                        .map(PointRecord::getAmount)
                        .containsExactly(CHARGE_AMOUNT),
                () -> assertThat(findMember.getTotalPoint()).isEqualTo(CHARGE_AMOUNT),
                () -> assertThat(findMember.getAvailablePoint()).isEqualTo(CHARGE_AMOUNT)
        );
    }

    @Nested
    @DisplayName("포인트 환불")
    class refundPoint {
        @Test
        @DisplayName("포인트가 부족함에 따라 환불을 진행할 수 없다")
        void throwExceptionByPointIsNotEnough() {
            assertThatThrownBy(() -> memberPointService.refundPoint(member.getId(), REFUND_AMOUNT))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.POINT_IS_NOT_ENOUGH.getMessage());
        }

        @Test
        @DisplayName("포인트 환불에 성공한다")
        void success() {
            // given
            memberPointService.chargePoint(member.getId(), CHARGE_AMOUNT);

            // when
            memberPointService.refundPoint(member.getId(), REFUND_AMOUNT);

            // then
            final Member findMember = memberRepository.findById(member.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(findMember.getPointRecords()).hasSize(2),
                    () -> assertThat(findMember.getPointRecords())
                            .map(PointRecord::getType)
                            .containsExactly(CHARGE, REFUND),
                    () -> assertThat(findMember.getPointRecords())
                            .map(PointRecord::getAmount)
                            .containsExactly(CHARGE_AMOUNT, REFUND_AMOUNT),
                    () -> assertThat(findMember.getTotalPoint()).isEqualTo(CHARGE_AMOUNT - REFUND_AMOUNT),
                    () -> assertThat(findMember.getAvailablePoint()).isEqualTo(CHARGE_AMOUNT - REFUND_AMOUNT)
            );
        }
    }
}

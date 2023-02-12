package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberPointService 테스트")
@RequiredArgsConstructor
class MemberPointServiceTest extends ServiceIntegrateTest {
    private final MemberPointService memberPointService;

    @Test
    @DisplayName("포인트 충전을 진행한다")
    void test1() {
        // given
        Member member = createMember();
        final int initAmount = member.getAvailablePoint();
        final int chargeAmount = 50_000_000;

        // when
        memberPointService.chargePoint(member.getId(), chargeAmount);

        // then
        List<PointDetail> pointDetails = pointDetailRepository.findByMemberId(member.getId());
        assertAll(
                () -> assertThat(pointDetails.size()).isEqualTo(1),
                // 포인트 충전
                () -> assertThat(pointDetails.get(0).getPointType()).isEqualTo(PointType.CHARGE),
                () -> assertThat(pointDetails.get(0).getAmount()).isEqualTo(chargeAmount),
                () -> assertThat(pointDetails.get(0).getMember().getId()).isEqualTo(member.getId()),
                // 최종 사용자 포인트 현황
                () -> assertThat(member.getAvailablePoint()).isEqualTo(initAmount + chargeAmount),
                () -> assertThat(memberRepository.getTotalPointsByMemberId(member.getId())).isEqualTo(initAmount + chargeAmount)
        );
    }

    @Nested
    @DisplayName("포인트 환불을 진행한다")
    class refundPoint {
        @Test
        @DisplayName("부족한 포인트로 인해 환불에 실패한다")
        void test1() {
            // given
            Member member = createMember();
            final int initAmount = member.getAvailablePoint();
            final int refundAmount = 50_000_000;

            // when - then
            assertThatThrownBy(() -> memberPointService.refundPoint(member.getId(), refundAmount))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.INVALID_POINT_DECREASE.getMessage());
        }

        @Test
        @DisplayName("포인트 환불을 성공한다")
        void test2() {
            // given
            Member member = createMemberAndChargePoint();
            final int initAmount = member.getAvailablePoint();
            final int refundAmount = 50_000_000;

            // when
            memberPointService.refundPoint(member.getId(), refundAmount);

            // then
            List<PointDetail> pointDetails = pointDetailRepository.findByMemberId(member.getId());
            assertAll(
                    () -> assertThat(pointDetails.size()).isEqualTo(2),
                    // 포인트 충전
                    () -> assertThat(pointDetails.get(0).getPointType()).isEqualTo(PointType.CHARGE),
                    () -> assertThat(pointDetails.get(0).getAmount()).isEqualTo(INIT_AVAILABLE_POINT),
                    () -> assertThat(pointDetails.get(0).getMember().getId()).isEqualTo(member.getId()),
                    // 포인트 환불
                    () -> assertThat(pointDetails.get(1).getPointType()).isEqualTo(PointType.REFUND),
                    () -> assertThat(pointDetails.get(1).getAmount()).isEqualTo(refundAmount),
                    () -> assertThat(pointDetails.get(1).getMember().getId()).isEqualTo(member.getId()),
                    // 최종 사용자 포인트 현황
                    () -> assertThat(member.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - refundAmount),
                    () -> assertThat(memberRepository.getTotalPointsByMemberId(member.getId())).isEqualTo(INIT_AVAILABLE_POINT - refundAmount)
            );
        }
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberAndChargePoint() {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }
}
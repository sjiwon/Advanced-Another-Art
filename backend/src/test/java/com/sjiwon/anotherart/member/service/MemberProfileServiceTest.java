package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.infra.query.dto.response.UserPointHistory;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member [Service Layer] -> MemberProfileService 테스트")
@RequiredArgsConstructor
class MemberProfileServiceTest extends ServiceIntegrateTest {
    private final MemberProfileService memberProfileService;
    private static final Member DEFAULT_MEMBER = MemberFixture.A.toMember();

    @Test
    @DisplayName("사용자의 기본적인 프로필 정보를 조회한다")
    void test1() {
        // given
        Member member = createMember();

        final int CHARGE_AMOUNT = 500_000;
        final int REFUND_AMOUNT = 200_000;
        doCharge(member, CHARGE_AMOUNT);
        doRefund(member, REFUND_AMOUNT);

        // when
        UserProfile userProfile = memberProfileService.getUserProfile(member.getId());

        // then
        assertAll(
                () -> assertThat(userProfile.getId()).isEqualTo(member.getId()),
                () -> assertThat(userProfile.getName()).isEqualTo(member.getName()),
                () -> assertThat(userProfile.getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(userProfile.getLoginId()).isEqualTo(member.getLoginId()),
                () -> assertThat(userProfile.getEmail()).isEqualTo(member.getEmailValue()),
                () -> assertThat(userProfile.getSchool()).isEqualTo(member.getSchool()),
                () -> assertThat(userProfile.getAddress().getPostcode()).isEqualTo(member.getAddress().getPostcode()),
                () -> assertThat(userProfile.getAddress().getDefaultAddress()).isEqualTo(member.getAddress().getDefaultAddress()),
                () -> assertThat(userProfile.getAddress().getDetailAddress()).isEqualTo(member.getAddress().getDetailAddress()),
                () -> assertThat(userProfile.getAvailablePoint()).isEqualTo(CHARGE_AMOUNT - REFUND_AMOUNT),
                () -> assertThat(userProfile.getTotalPoint()).isEqualTo(CHARGE_AMOUNT - REFUND_AMOUNT)
        );
    }

    @Test
    @DisplayName("사용자의 포인트 히스토리를 조회한다")
    void test2() {
        // 1. 회원가입
        Member member = createMember();
        List<UserPointHistory> list1 = memberRepository.findUserPointHistoryByMemberId(member.getId());
        assertThat(list1.size()).isEqualTo(0);

        final int chargeAmount = 100_000_000;
        final int refundAmount = 30_000_000;

        // 2. 포인트 충전 1회
        doCharge(member, chargeAmount);
        List<UserPointHistory> list2 = memberRepository.findUserPointHistoryByMemberId(member.getId());
        assertAll(
                () -> assertThat(list2.size()).isEqualTo(1),
                () -> assertThat(list2.get(0).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list2.get(0).getDealAmount()).isEqualTo(chargeAmount)
        );

        // 3. 포인트 충전 2회
        doCharge(member, chargeAmount);
        List<UserPointHistory> list3 = memberRepository.findUserPointHistoryByMemberId(member.getId());
        assertAll(
                () -> assertThat(list3.size()).isEqualTo(2),
                () -> assertThat(list3.get(0).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list3.get(0).getDealAmount()).isEqualTo(chargeAmount),
                () -> assertThat(list3.get(1).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list3.get(1).getDealAmount()).isEqualTo(chargeAmount)
        );

        // 4. 포인트 환불 1회
        doRefund(member, refundAmount);
        List<UserPointHistory> list4 = memberRepository.findUserPointHistoryByMemberId(member.getId());
        assertAll(
                () -> assertThat(list4.size()).isEqualTo(3),
                () -> assertThat(list4.get(0).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list4.get(0).getDealAmount()).isEqualTo(chargeAmount),
                () -> assertThat(list4.get(1).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list4.get(1).getDealAmount()).isEqualTo(chargeAmount),
                () -> assertThat(list4.get(2).getPointType()).isEqualTo(PointType.REFUND.getDescription()),
                () -> assertThat(list4.get(2).getDealAmount()).isEqualTo(refundAmount)
        );

        // 5. 포인트 환불 2회
        doRefund(member, refundAmount);
        List<UserPointHistory> list5 = memberRepository.findUserPointHistoryByMemberId(member.getId());
        assertAll(
                () -> assertThat(list5.size()).isEqualTo(4),
                () -> assertThat(list5.get(0).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list5.get(0).getDealAmount()).isEqualTo(chargeAmount),
                () -> assertThat(list5.get(1).getPointType()).isEqualTo(PointType.CHARGE.getDescription()),
                () -> assertThat(list5.get(1).getDealAmount()).isEqualTo(chargeAmount),
                () -> assertThat(list5.get(2).getPointType()).isEqualTo(PointType.REFUND.getDescription()),
                () -> assertThat(list5.get(2).getDealAmount()).isEqualTo(refundAmount),
                () -> assertThat(list5.get(3).getPointType()).isEqualTo(PointType.REFUND.getDescription()),
                () -> assertThat(list5.get(3).getDealAmount()).isEqualTo(refundAmount)
        );
    }

    private void doCharge(Member member, int chargeAmount) {
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
    }

    private void doRefund(Member member, int refundAmount) {
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.REFUND, refundAmount));
    }

    private Member createMember() {
        return memberRepository.save(DEFAULT_MEMBER);
    }
}
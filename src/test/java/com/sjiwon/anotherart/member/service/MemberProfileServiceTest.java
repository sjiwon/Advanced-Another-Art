package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
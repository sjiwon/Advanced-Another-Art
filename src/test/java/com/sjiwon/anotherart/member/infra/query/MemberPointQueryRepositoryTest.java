package com.sjiwon.anotherart.member.infra.query;

import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Point [Repository Layer] -> MemberPointQueryRepository 테스트")
class MemberPointQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointDetailRepository pointDetailRepository;

    @Test
    @DisplayName("사용자의 현재 총 포인트 보유량을 확인한다")
    void test() {
        // 1. 회원가입
        Member member = createMember();
        assertAll(
                () -> assertThat(member.getAvailablePoint()).isEqualTo(0),
                () -> assertThat(memberRepository.getTotalPointByMemberId(member.getId())).isEqualTo(0)
        );

        // 2. 포인트 충전 1회
        final int chargeAmount1 = 100_000_000;
        doCharge(member, chargeAmount1);
        assertAll(
                () -> assertThat(member.getAvailablePoint()).isEqualTo(chargeAmount1),
                () -> assertThat(memberRepository.getTotalPointByMemberId(member.getId())).isEqualTo(chargeAmount1)
        );

        // 3. 포인트 충전 2회
        final int chargeAmount2 = 300_000_000;
        doCharge(member, chargeAmount2);
        assertAll(
                () -> assertThat(member.getAvailablePoint()).isEqualTo(chargeAmount1 + chargeAmount2),
                () -> assertThat(memberRepository.getTotalPointByMemberId(member.getId())).isEqualTo(chargeAmount1 + chargeAmount2)
        );

        // 4. 포인트 환불 1회
        final int refundAmount1 = 50_000_000;
        doRefund(member, refundAmount1);
        assertAll(
                () -> assertThat(member.getAvailablePoint()).isEqualTo(chargeAmount1 + chargeAmount2 - refundAmount1),
                () -> assertThat(memberRepository.getTotalPointByMemberId(member.getId())).isEqualTo(chargeAmount1 + chargeAmount2 - refundAmount1)
        );

        // 5. 포인트 환불 2회
        final int refundAmount2 = 20_000_000;
        doRefund(member, refundAmount2);
        assertAll(
                () -> assertThat(member.getAvailablePoint()).isEqualTo(chargeAmount1 + chargeAmount2 - refundAmount1 - refundAmount2),
                () -> assertThat(memberRepository.getTotalPointByMemberId(member.getId())).isEqualTo(chargeAmount1 + chargeAmount2 - refundAmount1 - refundAmount2)
        );
    }

    private void doCharge(Member member, int chargeAmount) {
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
    }

    private void doRefund(Member member, int refundAmount) {
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.REFUND, refundAmount));
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}
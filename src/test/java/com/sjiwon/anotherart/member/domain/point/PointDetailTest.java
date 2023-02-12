package com.sjiwon.anotherart.member.domain.point;

import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("PointDetail 도메인 테스트")
class PointDetailTest {
    @Test
    @DisplayName("포인트 충전 후 멤버의 포인트 내역")
    void test1(){
        // given
        Member memberA = MemberFixture.A.toMember();

        // when
        final int chargeAmount = 10000;
        PointDetail pointDetail = PointDetail.insertPointDetail(memberA, PointType.CHARGE, chargeAmount);

        // then
        assertAll(
                () -> assertThat(pointDetail.getPointType()).isEqualTo(PointType.CHARGE),
                () -> assertThat(pointDetail.getAmount()).isEqualTo(chargeAmount),
                () -> assertThat(pointDetail.isPointIncreaseType()).isTrue(),
                () -> assertThat(memberA.getAvailablePoint()).isEqualTo(chargeAmount)
        );
    }

    @Test
    @DisplayName("포인트 환불 후 멤버의 포인트 내역")
    void test2(){
        // given
        Member memberA = MemberFixture.A.toMember();
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // when
        final int refundAmount = 5000;
        PointDetail pointDetail = PointDetail.insertPointDetail(memberA, PointType.REFUND, refundAmount);

        // then
        assertAll(
                () -> assertThat(pointDetail.getPointType()).isEqualTo(PointType.REFUND),
                () -> assertThat(pointDetail.getAmount()).isEqualTo(refundAmount),
                () -> assertThat(pointDetail.isPointIncreaseType()).isFalse(),
                () -> assertThat(memberA.getAvailablePoint()).isEqualTo(increasePoint - refundAmount)
        );
    }
}
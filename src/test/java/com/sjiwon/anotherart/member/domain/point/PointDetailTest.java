package com.sjiwon.anotherart.member.domain.point;

import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PointDetail 도메인 테스트")
class PointDetailTest {
    @Test
    @DisplayName("회원가입한 멤버의 포인트 내역")
    void test1(){
        // given
        Member memberA = MemberFixture.A.toMember();

        // when
        PointDetail pointDetail = PointDetail.createPointDetail(memberA);

        // then
        assertThat(pointDetail.getPointType()).isEqualTo(PointType.JOIN);
        assertThat(pointDetail.getAmount()).isEqualTo(0);
    }

    @Test
    @DisplayName("포인트 충전 후 멤버의 포인트 내역")
    void test2(){
        // given
        Member memberA = MemberFixture.A.toMember();

        // when
        final int chargeAmount = 10000;
        PointDetail pointDetail = PointDetail.insertPointDetail(memberA, PointType.CHARGE, chargeAmount);

        // then
        assertThat(pointDetail.getPointType()).isEqualTo(PointType.CHARGE);
        assertThat(pointDetail.getAmount()).isEqualTo(chargeAmount);
        assertThat(pointDetail.isPointIncreaseType()).isTrue();
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(chargeAmount);
    }

    @Test
    @DisplayName("포인트 환불 후 멤버의 포인트 내역")
    void test3(){
        // given
        Member memberA = MemberFixture.A.toMember();
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // when
        final int refundAmount = 5000;
        PointDetail pointDetail = PointDetail.insertPointDetail(memberA, PointType.REFUND, refundAmount);

        // then
        assertThat(pointDetail.getPointType()).isEqualTo(PointType.REFUND);
        assertThat(pointDetail.getAmount()).isEqualTo(refundAmount);
        assertThat(pointDetail.isPointIncreaseType()).isFalse();
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint - refundAmount);
    }

    @Test
    @DisplayName("작품 구매 후 멤버의 포인트 내역")
    void test4(){
        // given
        Member memberA = MemberFixture.A.toMember();
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // when
        final int artPrice = 5000;
        PointDetail pointDetail = PointDetail.insertPointDetail(memberA, PointType.PURCHASE, artPrice);

        // then
        assertThat(pointDetail.getPointType()).isEqualTo(PointType.PURCHASE);
        assertThat(pointDetail.getAmount()).isEqualTo(artPrice);
        assertThat(pointDetail.isPointIncreaseType()).isFalse();
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint - artPrice);
    }

    @Test
    @DisplayName("작품 판매 후 멤버의 포인트 내역")
    void test5(){
        // given
        Member memberA = MemberFixture.A.toMember();
        final int increasePoint = 10000;
        memberA.increasePoint(increasePoint);

        // when
        final int artPrice = 5000;
        PointDetail pointDetail = PointDetail.insertPointDetail(memberA, PointType.SOLD, artPrice);

        // then
        assertThat(pointDetail.getPointType()).isEqualTo(PointType.SOLD);
        assertThat(pointDetail.getAmount()).isEqualTo(artPrice);
        assertThat(pointDetail.isPointIncreaseType()).isTrue();
        assertThat(memberA.getAvailablePoint().getValue()).isEqualTo(increasePoint + artPrice);
    }
}
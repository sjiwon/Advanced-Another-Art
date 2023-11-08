package com.sjiwon.anotherart.point.domain.model;

import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Point -> 도메인 [PointRecord] 테스트")
public class PointRecordTest {
    private static final int MEMBER_INIT_POINT = 1_000_000;
    private Member member;

    @BeforeEach
    void setUp() {
        member = MEMBER_A.toMember().apply(1L);
        member.increaseTotalPoint(MEMBER_INIT_POINT);
    }

    @Test
    @DisplayName("포인트 충전에 대한 PointRecord를 생성한다 [Member 포인트 직접 변동 O]")
    void charge() {
        // given
        final int chargeAmount = 50_000;

        // when
        final PointRecord pointRecord = PointRecord.addPointRecord(member, PointType.CHARGE, chargeAmount);

        // then
        assertAll(
                () -> assertThat(pointRecord.getType()).isEqualTo(PointType.CHARGE),
                () -> assertThat(pointRecord.getAmount()).isEqualTo(chargeAmount),
                () -> assertThat(member.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT + chargeAmount),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT + chargeAmount)
        );
    }

    @Test
    @DisplayName("포인트 환불에 대한 PointRecord를 생성한다 [Member 포인트 직접 변동 O]")
    void refund() {
        // given
        final int refundAmount = 50_000;

        // when
        final PointRecord pointRecord = PointRecord.addPointRecord(member, PointType.REFUND, refundAmount);

        // then
        assertAll(
                () -> assertThat(pointRecord.getType()).isEqualTo(PointType.REFUND),
                () -> assertThat(pointRecord.getAmount()).isEqualTo(refundAmount),
                () -> assertThat(member.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT - refundAmount),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT - refundAmount)
        );
    }

    @Test
    @DisplayName("작품 구매에 대한 PointRecord를 생성한다 [Member 포인트 직접 변동 X - 구매 로직에서 적용]")
    void purchase() {
        // given
        final int artPrice = 50_000;

        // when
        final PointRecord pointRecord = PointRecord.addPointRecord(member, PointType.PURCHASE, artPrice);

        // then
        assertAll(
                () -> assertThat(pointRecord.getType()).isEqualTo(PointType.PURCHASE),
                () -> assertThat(pointRecord.getAmount()).isEqualTo(artPrice),
                () -> assertThat(member.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT)
        );
    }

    @Test
    @DisplayName("작품 판매에 대한 PointRecord를 생성한다 [Member 포인트 직접 변동 X - 구매 로직에서 적용]")
    void sold() {
        // given
        final int artPrice = 50_000;

        // when
        final PointRecord pointRecord = PointRecord.addPointRecord(member, PointType.SOLD, artPrice);

        // then
        assertAll(
                () -> assertThat(pointRecord.getType()).isEqualTo(PointType.SOLD),
                () -> assertThat(pointRecord.getAmount()).isEqualTo(artPrice),
                () -> assertThat(member.getTotalPoint()).isEqualTo(MEMBER_INIT_POINT),
                () -> assertThat(member.getAvailablePoint()).isEqualTo(MEMBER_INIT_POINT)
        );
    }
}

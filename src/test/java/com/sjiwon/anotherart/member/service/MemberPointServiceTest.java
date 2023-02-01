package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class MemberPointServiceTest extends ServiceTest {
    @InjectMocks
    MemberPointService memberPointService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PointDetailRepository pointDetailRepository;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @Test
    @DisplayName("포인트 충전을 진행한다")
    void test1() {
        // given
        Member member = MemberFixture.A.toMember(ENCODER);
        final Long memberId = 1L;
        final int initAmount = member.getAvailablePoint().getValue();
        final int chargeAmount = 15000;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        memberPointService.chargePoint(memberId, chargeAmount);

        // then
        assertThat(member.getAvailablePoint().getValue()).isEqualTo(initAmount + chargeAmount);
    }

    @Nested
    @DisplayName("포인트 환불을 진행한다")
    class refundPoint {
        @Test
        @DisplayName("부족한 포인트로 인해 환불에 실패한다")
        void test1() {
            // given
            Member member = MemberFixture.A.toMember(ENCODER);
            member.increasePoint(10000);
            final Long memberId = 1L;
            final int initAmount = member.getAvailablePoint().getValue();
            final int refundAmount = 15000;
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            // when - then
            assertThatThrownBy(() -> memberPointService.refundPoint(memberId, refundAmount))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.INVALID_POINT_DECREASE.getMessage());
        }

        @Test
        @DisplayName("포인트 환불을 성공한다")
        void test2() {
            // given
            Member member = MemberFixture.A.toMember(ENCODER);
            member.increasePoint(20000);
            final Long memberId = 1L;
            final int initAmount = member.getAvailablePoint().getValue();
            final int refundAmount = 15000;
            given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

            // when
            memberPointService.refundPoint(memberId, refundAmount);

            // then
            assertThat(member.getAvailablePoint().getValue()).isEqualTo(initAmount - refundAmount);
        }
    }
}
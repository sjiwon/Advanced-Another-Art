package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
}
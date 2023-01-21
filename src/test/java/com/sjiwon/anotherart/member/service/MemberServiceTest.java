package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.point.domain.PointDetailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("Member [Service Layer] -> MemberService 테스트")
class MemberServiceTest extends ServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberValidator memberValidator;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PointDetailRepository pointDetailRepository;

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    
    @Test
    @DisplayName("사용자에 대한 회원가입을 성공한다")
    void test(){
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        final Long savedMemberId = 1L;
        given(memberRepository.save(member)).willReturn(member);
        ReflectionTestUtils.setField(member, "id", savedMemberId);

        // when
        Long memberId = memberService.signUp(member);

        // then
        assertThat(memberId).isNotNull();
        assertThat(memberId).isEqualTo(savedMemberId);
    }
}
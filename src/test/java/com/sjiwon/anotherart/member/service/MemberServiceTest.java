package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.point.domain.PointDetailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

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

    @Test
    @DisplayName("사용자에 대한 회원가입을 성공한다")
    void test1() {
        // given
        final Member member = MemberFixture.A.toMember(PasswordEncoderUtils.getEncoder());
        final Long savedMemberId = 1L;
        given(memberRepository.save(member)).willReturn(member);
        ReflectionTestUtils.setField(member, "id", savedMemberId);

        // when
        Long memberId = memberService.signUp(member);

        // then
        assertThat(memberId).isNotNull();
        assertThat(memberId).isEqualTo(savedMemberId);
    }

    @Test
    @DisplayName("사용자의 닉네임을 수정한다")
    void test2() {
        // given
        final Member member = MemberFixture.A.toMember(PasswordEncoderUtils.getEncoder());
        final Long memberId = 1L;
        final String changeNickname = member.getNickname() + "hello world";
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        memberService.changeNickname(memberId, changeNickname);

        // then
        assertThat(member.getNickname()).isEqualTo(changeNickname);
    }
}
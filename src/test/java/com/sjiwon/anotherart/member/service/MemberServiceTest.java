package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@DisplayName("Member [Service Layer] -> MemberService 테스트")
class MemberServiceTest extends ServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberValidator memberValidator;

    @Mock
    private MemberFindService memberFindService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PointDetailRepository pointDetailRepository;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();

    @Test
    @DisplayName("사용자에 대한 회원가입을 성공한다")
    void test1() {
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

    @Test
    @DisplayName("사용자의 닉네임을 수정한다")
    void test2() {
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        final Long memberId = 1L;
        final String changeNickname = member.getNickname() + "hello world";
        given(memberFindService.findById(memberId)).willReturn(member);

        // when
        memberService.changeNickname(memberId, changeNickname);

        // then
        assertThat(member.getNickname()).isEqualTo(changeNickname);
    }
    
    @Test
    @DisplayName("이름, 이메일에 해당되는 사용자의 로그인 아이디를 찾는다")
    void test3() {
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        given(memberFindService.findByNameAndEmail(member.getName(), member.getEmail())).willReturn(member);

        // when
        String loginId = memberService.findLoginId(member.getName(), member.getEmail());

        // then
        assertThat(loginId).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("이름, 로그인 아이디, 이메일에 해당하는 사용자가 존재하는지 확인한다")
    void test4() {
        // given
        final Member member = MemberFixture.A.toMember(ENCODER);
        final String name = member.getName();
        final String loginId = member.getLoginId();
        final Email email = member.getEmail();
        given(memberRepository.existsByNameAndLoginIdAndEmail(name, loginId, email)).willReturn(true);

        // when - then
        assertDoesNotThrow(() -> memberService.authMemberForResetPassword(name, loginId, email));
        assertThatThrownBy(() -> memberService.authMemberForResetPassword(name + "diff", loginId, email))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> memberService.authMemberForResetPassword(name, loginId + "diff", email))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> memberService.authMemberForResetPassword(name, loginId, Email.from("diff" + email.getValue())))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }
}
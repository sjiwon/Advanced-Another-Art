package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member [Service Layer] -> MemberService 테스트")
@RequiredArgsConstructor
class MemberServiceTest extends ServiceIntegrateTest {
    private final MemberService memberService;

    @Nested
    @DisplayName("회원가입")
    class signUp {
        @Test
        @DisplayName("중복되는 닉네임으로 인해 회원가입에 실패한다")
        void test1() {
            // given
            Member member = createMemberA();
            Member sameMember = MemberFixture.A.toMember();

            // when - then
            assertThatThrownBy(() -> memberService.signUp(sameMember))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("회원가입에 성공한다")
        void test2() {
            // given
            Member member = MemberFixture.A.toMember();

            // when
            Long memberId = memberService.signUp(member);

            // then
            assertThat(memberId).isNotNull();
        }
    }

    @Nested
    @DisplayName("닉네임 수정")
    class changeNickname {
        @Test
        @DisplayName("이전과 동일한 닉네임으로 수정할 수 없다")
        void test1() {
            // given
            Member member = createMemberA();

            // when - then
            assertThatThrownBy(() -> memberService.changeNickname(member.getId(), member.getNickname()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("중복되는 닉네임으로 수정할 수 없다")
        void test2() {
            // given
            Member memberA = createMemberA();
            Member memberB = createMemberB();

            // when - then
            assertThatThrownBy(() -> memberService.changeNickname(memberA.getId(), memberB.getNickname()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("닉네임 수정에 성공한다")
        void test3() {
            // given
            Member member = createMemberA();
            final String changeNickname = member.getNickname() + "change";

            // when
            memberService.changeNickname(member.getId(), changeNickname);

            // then
            assertThat(member.getNickname()).isEqualTo(changeNickname);
        }
    }

    @Test
    @DisplayName("이름, 이메일에 해당되는 사용자의 로그인 아이디를 찾는다")
    void test6() {
        // given
        Member member = createMemberA();

        // when
        String loginId = memberService.findLoginId(member.getName(), member.getEmailValue());

        // then
        assertThat(loginId).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("이름, 로그인 아이디, 이메일에 해당하는 사용자가 존재하는지 확인한다")
    void test7() {
        // given
        Member member = createMemberA();
        final String name = member.getName();
        final String loginId = member.getLoginId();
        final String email = member.getEmailValue();

        // when - then
        assertDoesNotThrow(() -> memberService.authMemberForResetPassword(name, loginId, email));
        assertThatThrownBy(() -> memberService.authMemberForResetPassword(name + "diff", loginId, email))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> memberService.authMemberForResetPassword(name, loginId + "diff", email))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> memberService.authMemberForResetPassword(name, loginId, "diff" + email))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("비밀번호를 재설정한다")
    void test8() {
        // given
        Member member = createMemberA();
        final String changePassword = MemberFixture.A.getPassword() + "change";

        // when
        memberService.resetPassword(member.getLoginId(), changePassword);

        // then
        PasswordEncoder encoder = PasswordEncoderUtils.getEncoder();
        assertThat(encoder.matches(changePassword, member.getPasswordValue())).isTrue();
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }
}
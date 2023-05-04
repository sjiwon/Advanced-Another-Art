package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.*;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Member [Service Layer] -> MemberService 테스트")
class MemberServiceTest extends ServiceTest {
    @Autowired
    private MemberService memberService;

    @Nested
    @DisplayName("회원가입")
    class signUp {
        @Test
        @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
        void throwExceptionByDuplicateNickname() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    member.getNicknameValue(),
                    "fake",
                    "99988887777",
                    "fake@gmail.com"
            );

            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 로그인 아이디면 회원가입에 실패한다")
        void throwExceptionByDuplicateLoginId() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    "fake",
                    member.getLoginId(),
                    "99988887777",
                    "fake@gmail.com"
            );

            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
        void throwExceptionByDuplicatePhone() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    "fake",
                    "fake",
                    member.getPhone(),
                    "fake@gmail.com"
            );

            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        }

        @Test
        @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
        void throwExceptionByDuplicateEmail() {
            // given
            final Member member = memberRepository.save(MEMBER_A.toMember());

            // when - then
            final Member newMember = createDuplicateMember(
                    "fake",
                    "fake",
                    "99988887777",
                    member.getEmailValue()
            );

            assertThatThrownBy(() -> memberService.signUp(newMember))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        }

        @Test
        @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
        void success() {
            // given
            final Member member = MEMBER_A.toMember();

            // when
            Long savedMemberId = memberService.signUp(member);

            // then
            Member findMember = memberRepository.findById(savedMemberId).orElseThrow();
            assertThat(findMember).isEqualTo(member);
        }
    }

    private Member createDuplicateMember(String nickname, String loginId, String phone, String email) {
        return Member.createMember(
                MEMBER_A.getName(),
                Nickname.from(nickname),
                loginId,
                Password.encrypt(MEMBER_A.getPassword(), new BCryptPasswordEncoder()),
                "경기대학교",
                phone,
                Email.from(email),
                Address.of(12345, "기본 주소", "상세 주소")
        );
    }
}

package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.*;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
        @DisplayName("회원가입에 성공한다")
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

    @Nested
    @DisplayName("중복 체크")
    class duplicateCheck {
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(MEMBER_A.toMember());
        }

        @Test
        @DisplayName("닉네임 중복 체크를 진행한다")
        void checkNickname() {
            // given
            final String same = member.getNicknameValue();
            final String diff = "fake";

            // when - then
            assertThatThrownBy(() -> memberService.duplicateCheck("nickname", same))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
            assertDoesNotThrow(() -> memberService.duplicateCheck("nickname", diff));
        }

        @Test
        @DisplayName("로그인 아이디 중복 체크를 진행한다")
        void checkLoginId() {
            // given
            final String same = member.getLoginId();
            final String diff = "fake";

            // when - then
            assertThatThrownBy(() -> memberService.duplicateCheck("loginId", same))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
            assertDoesNotThrow(() -> memberService.duplicateCheck("loginId", diff));
        }

        @Test
        @DisplayName("전화번호 중복 체크를 진행한다")
        void checkPhone() {
            // given
            final String same = member.getPhone();
            final String diff = "fake";

            // when - then
            assertThatThrownBy(() -> memberService.duplicateCheck("phone", same))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
            assertDoesNotThrow(() -> memberService.duplicateCheck("phone", diff));
        }

        @Test
        @DisplayName("이메일 중복 체크를 진행한다")
        void checkEmail() {
            // given
            final String same = member.getEmailValue();
            final String diff = "fake@gmail.com";

            // when - then
            assertThatThrownBy(() -> memberService.duplicateCheck("email", same))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
            assertDoesNotThrow(() -> memberService.duplicateCheck("email", diff));
        }
    }

    @Nested
    @DisplayName("닉네임 변경")
    class changeNickname {
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(MEMBER_A.toMember());
        }

        @Test
        @DisplayName("타인이 사용하고 있는 닉네임으로 변경하려고 하면 예외가 발생한다")
        void throwExceptionByDuplicateNickname() {
            // given
            Member compare = memberRepository.save(MEMBER_B.toMember());

            // when - then
            assertThatThrownBy(() -> memberService.changeNickname(member.getId(), compare.getNicknameValue()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        @Test
        @DisplayName("이전과 동일한 닉네임으로 변경하려고 하면 예외가 발생한다")
        void throwExceptionByNicknameSameAsBefore() {
            assertThatThrownBy(() -> memberService.changeNickname(member.getId(), member.getNicknameValue()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(MemberErrorCode.NICKNAME_SAME_AS_BEFORE.getMessage());
        }

        @Test
        @DisplayName("닉네임 변경에 성공한다")
        void success() {
            // given
            final String update = member.getNicknameValue() + "update";

            // when
            memberService.changeNickname(member.getId(), update);

            // then
            Member findMember = memberRepository.findById(member.getId()).orElseThrow();
            assertThat(findMember.getNicknameValue()).isEqualTo(update);
        }
    }

    @Test
    @DisplayName("주소 변경에 성공한다")
    void changeAddress() {
        // given
        Member member = memberRepository.save(MEMBER_A.toMember());

        // when
        final int postcode = 99999;
        final String defaultAddress = "Hello World";
        final String detailAddress = "Spring Data JPA";
        memberService.changeAddress(member.getId(), postcode, defaultAddress, detailAddress);

        // then
        Member findMember = memberRepository.findById(member.getId()).orElseThrow();
        Address updateAddress = findMember.getAddress();

        assertAll(
                () -> assertThat(updateAddress.getPostcode()).isNotEqualTo(MEMBER_A.getPostcode()),
                () -> assertThat(updateAddress.getPostcode()).isEqualTo(postcode),
                () -> assertThat(updateAddress.getDefaultAddress()).isNotEqualTo(MEMBER_A.getDefaultAddress()),
                () -> assertThat(updateAddress.getDefaultAddress()).isEqualTo(defaultAddress),
                () -> assertThat(updateAddress.getDetailAddress()).isNotEqualTo(MEMBER_A.getDetailAddress()),
                () -> assertThat(updateAddress.getDetailAddress()).isEqualTo(detailAddress)
        );
    }

    @Test
    @DisplayName("사용자의 로그인 아이디를 조회한다")
    void findLoginId() {
        // given
        Member member = memberRepository.save(MEMBER_A.toMember());

        // when
        String findLoginId = memberService.findLoginId(member.getName(), member.getEmail());

        // then
        assertThat(findLoginId).isEqualTo(member.getLoginId());
    }

    private Member createDuplicateMember(String nickname, String loginId, String phone, String email) {
        return Member.createMember(
                MEMBER_A.getName(),
                Nickname.from(nickname),
                loginId,
                Password.encrypt(MEMBER_A.getPassword(), PasswordEncoderFactories.createDelegatingPasswordEncoder()),
                "경기대학교",
                phone,
                Email.from(email),
                Address.of(12345, "기본 주소", "상세 주소")
        );
    }
}

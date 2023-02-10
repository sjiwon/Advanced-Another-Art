package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member [Service Layer] -> MemberValidator 테스트")
@RequiredArgsConstructor
class MemberValidatorTest extends ServiceIntegrateTest {
    private final MemberValidator memberValidator;

    @Test
    @DisplayName("닉네임이 중복되는지 확인한다")
    void test1() {
        // given
        Member member = createMember();

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicateNickname(member.getNickname()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicateNickname(member.getNickname() + "diff"));
    }

    @Test
    @DisplayName("로그인 아이디가 중복되는지 확인한다")
    void test2() {
        // given
        Member member = createMember();

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicateLoginId(member.getLoginId()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicateLoginId(member.getLoginId() + "diff"));
    }

    @Test
    @DisplayName("전화번호가 중복되는지 확인한다")
    void test3() {
        // given
        Member member = createMember();

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicatePhone(member.getPhone()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicatePhone(member.getPhone().replaceAll("0", "9")));
    }

    @Test
    @DisplayName("이메일이 중복되는지 확인한다")
    void test4() {
        // given
        Member member = createMember();

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicateEmail(member.getEmail()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicateEmail(Email.from("diff" + member.getEmailValue())));
    }

    @Test
    @DisplayName("중복 통합 테스트")
    void test5() {
        // given
        createMember();
        Member compareMember = MemberFixture.B.toMember();

        // when - then
        assertDoesNotThrow(() -> memberValidator.validateDuplicateResources(compareMember));
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}
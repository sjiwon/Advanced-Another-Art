package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;

@DisplayName("Member [Service Layer] -> MemberValidator 테스트")
class MemberValidatorTest extends ServiceTest {
    @InjectMocks
    private MemberValidator memberValidator;

    @Mock
    private MemberRepository memberRepository;

    @Test
    @DisplayName("닉네임이 중복되는지 확인한다")
    void test1() {
        // given
        final String existsNickname = "exists";
        final String notExistsNickname = "not exists";
        given(memberRepository.existsByNickname(existsNickname)).willReturn(true);
        given(memberRepository.existsByNickname(notExistsNickname)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicateNickname(existsNickname))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicateNickname(notExistsNickname));
    }

    @Test
    @DisplayName("로그인 아이디가 중복되는지 확인한다")
    void test2() {
        // given
        final String existsLoginId = "exists";
        final String notExistsLoginId = "not exists";
        given(memberRepository.existsByLoginId(existsLoginId)).willReturn(true);
        given(memberRepository.existsByLoginId(notExistsLoginId)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicateLoginId(existsLoginId))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicateLoginId(notExistsLoginId));
    }

    @Test
    @DisplayName("전화번호가 중복되는지 확인한다")
    void test3() {
        // given
        final String existsPhone = "01012345678";
        final String notExistsPhone = "99988887777";
        given(memberRepository.existsByPhone(existsPhone)).willReturn(true);
        given(memberRepository.existsByPhone(notExistsPhone)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicatePhone(existsPhone))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicatePhone(notExistsPhone));
    }

    @Test
    @DisplayName("이메일이 중복되는지 확인한다")
    void test4() {
        // given
        final Email existsEmail = Email.from("hello@gmail.com");
        final Email notExistsEmail = Email.from("fake@gmail.com");
        given(memberRepository.existsByEmail(existsEmail)).willReturn(true);
        given(memberRepository.existsByEmail(notExistsEmail)).willReturn(false);

        // when - then
        assertThatThrownBy(() -> memberValidator.validateDuplicateEmail(existsEmail))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        assertDoesNotThrow(() -> memberValidator.validateDuplicateEmail(notExistsEmail));
    }
    
    @Test
    @DisplayName("중복 통합 테스트")
    void test5(){
        // given
        final Member member = MemberFixture.A.toMember();
        given(memberRepository.existsByNickname(member.getNickname())).willReturn(false);
        given(memberRepository.existsByLoginId(member.getLoginId())).willReturn(false);
        given(memberRepository.existsByPhone(member.getPhone())).willReturn(false);
        given(memberRepository.existsByEmail(member.getEmail())).willReturn(false);

        // when - then
        assertDoesNotThrow(() -> memberValidator.validateDuplicateResources(member));
    }
}
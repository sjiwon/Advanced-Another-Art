package com.sjiwon.anotherart.member.domain.service;

import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Member -> MemberResourceValidator 테스트")
public class MemberResourceValidatorTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MemberResourceValidator sut = new MemberResourceValidator(memberRepository);

    private final Member member = MEMBER_A.toMember().apply(1L);

    @Test
    @DisplayName("로그인 아이디에 대한 Unique 검증을 진행한다")
    void validateLoginIdIsUnique() {
        // given
        given(memberRepository.existsByLoginId(member.getLoginId())).willReturn(true);
        given(memberRepository.existsByLoginId("diff" + member.getLoginId())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.validateLoginIdIsUnique(member.getLoginId()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());
        assertDoesNotThrow(() -> sut.validateLoginIdIsUnique("diff" + member.getLoginId()));
    }

    @Test
    @DisplayName("이메일에 대한 Unique 검증을 진행한다")
    void validateEmailIsUnique() {
        // given
        given(memberRepository.existsByEmailValue(member.getEmail().getValue())).willReturn(true);
        given(memberRepository.existsByEmailValue("diff" + member.getEmail().getValue())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.validateEmailIsUnique(member.getEmail().getValue()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());
        assertDoesNotThrow(() -> sut.validateEmailIsUnique("diff" + member.getEmail().getValue()));
    }

    @Test
    @DisplayName("닉네임에 대한 Unique 검증을 진행한다")
    void validateNicknameIsUnique() {
        // given
        given(memberRepository.existsByNicknameValue(member.getNickname().getValue())).willReturn(true);
        given(memberRepository.existsByNicknameValue("diff" + member.getNickname().getValue())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.validateNicknameIsUnique(member.getNickname().getValue()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());
        assertDoesNotThrow(() -> sut.validateNicknameIsUnique("diff" + member.getNickname().getValue()));
    }

    @Test
    @DisplayName("전화번호에 대한 Unique 검증을 진행한다")
    void validatePhoneIsUnique() {
        // given
        given(memberRepository.existsByPhoneValue(member.getPhone().getValue())).willReturn(true);
        given(memberRepository.existsByPhoneValue(member.getPhone().getValue().replaceAll("0", "9"))).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.validatePhoneIsUnique(member.getPhone().getValue()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());
        assertDoesNotThrow(() -> sut.validatePhoneIsUnique(member.getPhone().getValue().replaceAll("0", "9")));
    }
}

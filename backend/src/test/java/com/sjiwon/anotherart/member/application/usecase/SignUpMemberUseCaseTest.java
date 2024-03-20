package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.common.mock.fake.FakePasswordEncryptor;
import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> SignUpMemberUseCase 테스트")
public class SignUpMemberUseCaseTest extends UnitTest {
    private final PasswordEncryptor passwordEncryptor = new FakePasswordEncryptor();
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MemberResourceValidator memberResourceValidator = new MemberResourceValidator(memberRepository);
    private final SignUpMemberUseCase sut = new SignUpMemberUseCase(memberResourceValidator, passwordEncryptor, memberRepository);

    private final SignUpMemberCommand command = new SignUpMemberCommand(
            MEMBER_A.getName(),
            MEMBER_A.getNickname(),
            MEMBER_A.getLoginId(),
            MEMBER_A.getPassword(),
            "경기대학교",
            MEMBER_A.getPhone(),
            MEMBER_A.getEmail(),
            MEMBER_A.getAddress()
    );

    @Test
    @DisplayName("이미 사용하고 있는 로그인 아이디면 회원가입에 실패한다")
    void throwExceptionByDuplicateLoginId() {
        // given
        given(memberRepository.existsByLoginId(command.loginId())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_LOGIN_ID.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByLoginId(command.loginId()),
                () -> verify(memberRepository, times(0)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(0)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(0)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 이메일이면 회원가입에 실패한다")
    void throwExceptionByDuplicateEmail() {
        // given
        given(memberRepository.existsByLoginId(command.loginId())).willReturn(false);
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByLoginId(command.loginId()),
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(0)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(0)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 닉네임이면 회원가입에 실패한다")
    void throwExceptionByDuplicateNickname() {
        // given
        given(memberRepository.existsByLoginId(command.loginId())).willReturn(false);
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(false);
        given(memberRepository.existsByNicknameValue(command.nickname().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_NICKNAME.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByLoginId(command.loginId()),
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(1)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(0)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("이미 사용하고 있는 전화번호면 회원가입에 실패한다")
    void throwExceptionByDuplicatePhone() {
        // given
        given(memberRepository.existsByLoginId(command.loginId())).willReturn(false);
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(false);
        given(memberRepository.existsByNicknameValue(command.nickname().getValue())).willReturn(false);
        given(memberRepository.existsByPhoneValue(command.phone().getValue())).willReturn(true);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

        assertAll(
                () -> verify(memberRepository, times(1)).existsByLoginId(command.loginId()),
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(1)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(1)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(0)).save(any(Member.class))
        );
    }

    @Test
    @DisplayName("모든 중복 검사를 통과한다면 회원가입에 성공한다")
    void success() {
        // given
        given(memberRepository.existsByLoginId(command.loginId())).willReturn(false);
        given(memberRepository.existsByEmailValue(command.email().getValue())).willReturn(false);
        given(memberRepository.existsByNicknameValue(command.nickname().getValue())).willReturn(false);
        given(memberRepository.existsByPhoneValue(command.phone().getValue())).willReturn(false);

        final Member member = MEMBER_A.toMember().apply(1L);
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // when
        final Long savedMemberId = sut.invoke(command);

        // then
        assertAll(
                () -> verify(memberRepository, times(1)).existsByLoginId(command.loginId()),
                () -> verify(memberRepository, times(1)).existsByEmailValue(command.email().getValue()),
                () -> verify(memberRepository, times(1)).existsByNicknameValue(command.nickname().getValue()),
                () -> verify(memberRepository, times(1)).existsByPhoneValue(command.phone().getValue()),
                () -> verify(memberRepository, times(1)).save(any(Member.class)),
                () -> assertThat(member.getPassword().getValue()).isNotEqualTo(command.password()),
                () -> assertThat(savedMemberId).isEqualTo(member.getId())
        );
    }
}

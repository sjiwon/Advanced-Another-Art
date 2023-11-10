package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.auth.application.adapter.MailAuthenticationProcessor;
import com.sjiwon.anotherart.auth.domain.AuthKey;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.mail.application.adapter.EmailSender;
import com.sjiwon.anotherart.member.application.usecase.command.AuthForRetrieveLoginIdCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ConfirmAuthCodeForLoginIdCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> RetrieveLoginIdUseCase 테스트")
public class RetrieveLoginIdUseCaseTest extends UseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MailAuthenticationProcessor mailAuthenticationProcessor = mock(MailAuthenticationProcessor.class);
    private final EmailSender emailSender = mock(EmailSender.class);
    private final RetrieveLoginIdUseCase sut = new RetrieveLoginIdUseCase(
            memberRepository,
            mailAuthenticationProcessor,
            emailSender
    );

    private final Member member = MEMBER_A.toMember().apply(1L);

    @Nested
    @DisplayName("인증번호 발송")
    class PprovideAuthCode {
        private final AuthForRetrieveLoginIdCommand command = new AuthForRetrieveLoginIdCommand(
                member.getName(),
                member.getEmail().getValue()
        );

        @Test
        @DisplayName("이름 + 이메일에 해당하는 사용자에게 아이디 찾기 인증번호를 발송한다")
        void success() {
            // given
            given(memberRepository.getByNameAndEmail(command.name(), command.email())).willReturn(member);

            final String key = AuthKey.LOGIN_AUTH_KEY.generateAuthKey(member.getEmail().getValue());
            final String authCode = "Hello";
            given(mailAuthenticationProcessor.storeAuthCode(key)).willReturn(authCode);

            // when
            sut.provideAuthCode(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmail(command.name(), command.email()),
                    () -> verify(mailAuthenticationProcessor, times(1)).storeAuthCode(key),
                    () -> verify(emailSender, times(1)).sendAuthCodeForLoginId(member.getEmail().getValue(), authCode)
            );
        }
    }

    @Nested
    @DisplayName("인증번호 확인 후 로그인 아이디 제공")
    class GetLoginId {
        private static final String AUTH_CODE = "Hello";
        private final ConfirmAuthCodeForLoginIdCommand command = new ConfirmAuthCodeForLoginIdCommand(
                member.getName(),
                member.getEmail().getValue(),
                AUTH_CODE
        );

        @Test
        @DisplayName("인증번호가 일치하지 않으면 사용자 인증에 실패한다")
        void throwExceptionByInvalidAuthCode() {
            // given
            given(memberRepository.getByNameAndEmail(command.name(), command.email())).willReturn(member);

            final String key = AuthKey.LOGIN_AUTH_KEY.generateAuthKey(member.getEmail().getValue());
            doThrow(AnotherArtException.type(AuthErrorCode.INVALID_AUTH_CODE))
                    .when(mailAuthenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when - then
            assertThatThrownBy(() -> sut.getLoginId(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(AuthErrorCode.INVALID_AUTH_CODE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmail(command.name(), command.email()),
                    () -> verify(mailAuthenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(mailAuthenticationProcessor, times(0)).deleteAuthCode(key)
            );
        }

        @Test
        @DisplayName("인증번호가 일치하면 사용자의 로그인 아이디를 제공한다")
        void success() {
            // given
            given(memberRepository.getByNameAndEmail(command.name(), command.email())).willReturn(member);

            final String key = AuthKey.LOGIN_AUTH_KEY.generateAuthKey(member.getEmail().getValue());
            doNothing()
                    .when(mailAuthenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when
            final String loginId = sut.getLoginId(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmail(command.name(), command.email()),
                    () -> verify(mailAuthenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(mailAuthenticationProcessor, times(1)).deleteAuthCode(key),
                    () -> assertThat(loginId).isEqualTo(member.getLoginId())
            );
        }
    }
}

package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.auth.application.adapter.MailAuthenticationProcessor;
import com.sjiwon.anotherart.auth.domain.AuthKey;
import com.sjiwon.anotherart.common.mock.fake.FakeEncryptor;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.exception.AuthException;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.mail.application.adapter.EmailSender;
import com.sjiwon.anotherart.member.application.usecase.command.AuthForResetPasswordCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ConfirmAuthCodeForResetPasswordCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ResetPasswordCommand;
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

@DisplayName("Member -> ResetPasswordUseCase 테스트")
public class ResetPasswordUseCaseTest {
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MailAuthenticationProcessor mailAuthenticationProcessor = mock(MailAuthenticationProcessor.class);
    private final EmailSender emailSender = mock(EmailSender.class);
    private final Encryptor encryptor = new FakeEncryptor();
    private final ResetPasswordUseCase sut = new ResetPasswordUseCase(
            memberRepository,
            mailAuthenticationProcessor,
            emailSender,
            encryptor
    );

    private final Member member = MEMBER_A.toMember().apply(1L);

    @Nested
    @DisplayName("인증번호 발송")
    class PprovideAuthCode {
        private final AuthForResetPasswordCommand command = new AuthForResetPasswordCommand(
                member.getName(),
                member.getEmail().getValue(),
                member.getLoginId()
        );

        @Test
        @DisplayName("이름 + 이메일 + 로그인 아이디에 해당하는 사용자에게 비밀번호 재설정 인증번호를 발송한다")
        void success() {
            // given
            given(memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId())).willReturn(member);

            final String key = AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(member.getEmail().getValue());
            final String authCode = "Hello";
            given(mailAuthenticationProcessor.storeAuthCode(key)).willReturn(authCode);

            // when
            sut.provideAuthCode(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId()),
                    () -> verify(mailAuthenticationProcessor, times(1)).storeAuthCode(key),
                    () -> verify(emailSender, times(1)).sendAuthCodeForPassword(member.getEmail().getValue(), authCode)
            );
        }
    }

    @Nested
    @DisplayName("인증번호 확인")
    class ConfirmAuthCode {
        private static final String AUTH_CODE = "Hello";
        private final ConfirmAuthCodeForResetPasswordCommand command = new ConfirmAuthCodeForResetPasswordCommand(
                member.getName(),
                member.getEmail().getValue(),
                member.getLoginId(),
                AUTH_CODE
        );

        @Test
        @DisplayName("인증번호가 일치하지 않으면 사용자 인증에 실패한다")
        void throwExceptionByInvalidAuthCode() {
            // given
            given(memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId())).willReturn(member);

            final String key = AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(member.getEmail().getValue());
            doThrow(new AuthException(AuthErrorCode.INVALID_AUTH_CODE))
                    .when(mailAuthenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when - then
            assertThatThrownBy(() -> sut.confirmAuthCode(command))
                    .isInstanceOf(AuthException.class)
                    .hasMessage(AuthErrorCode.INVALID_AUTH_CODE.getMessage());

            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId()),
                    () -> verify(mailAuthenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(mailAuthenticationProcessor, times(0)).deleteAuthCode(key)
            );
        }

        @Test
        @DisplayName("인증번호 검증에 성공한다")
        void success() {
            // given
            given(memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId())).willReturn(member);

            final String key = AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(member.getEmail().getValue());
            doNothing()
                    .when(mailAuthenticationProcessor)
                    .verifyAuthCode(key, command.authCode());

            // when
            sut.confirmAuthCode(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId()),
                    () -> verify(mailAuthenticationProcessor, times(1)).verifyAuthCode(key, command.authCode()),
                    () -> verify(mailAuthenticationProcessor, times(1)).deleteAuthCode(key)
            );
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정")
    class ResetPassword {
        private static final String NEW_PASSWORD = "HelloWorld123!@#";
        private final ResetPasswordCommand command = new ResetPasswordCommand(
                member.getName(),
                member.getEmail().getValue(),
                member.getLoginId(),
                NEW_PASSWORD
        );

        @Test
        @DisplayName("비밀번호를 재설정한다")
        void success() {
            // given
            given(memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId())).willReturn(member);

            // when
            sut.resetPassword(command);

            // then
            assertAll(
                    () -> verify(memberRepository, times(1)).getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId()),
                    () -> assertThat(encryptor.matches(NEW_PASSWORD, member.getPassword().getValue())).isTrue()
            );
        }
    }
}

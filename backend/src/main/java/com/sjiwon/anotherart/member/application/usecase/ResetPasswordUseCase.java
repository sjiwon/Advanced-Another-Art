package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.auth.application.adapter.MailAuthenticationProcessor;
import com.sjiwon.anotherart.auth.domain.AuthKey;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.mail.application.adapter.EmailSender;
import com.sjiwon.anotherart.member.application.usecase.command.AuthForResetPasswordCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ConfirmAuthCodeForResetPasswordCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ResetPasswordCommand;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ResetPasswordUseCase {
    private final MemberRepository memberRepository;
    private final MailAuthenticationProcessor mailAuthenticationProcessor;
    private final EmailSender emailSender;
    private final Encryptor encryptor;

    public void provideAuthCode(final AuthForResetPasswordCommand command) {
        final Member member = memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId());

        final String key = generateAuthKey(member.getEmail());
        final String authCode = mailAuthenticationProcessor.storeAuthCode(key);
        emailSender.sendAuthCodeForPassword(member.getEmail().getValue(), authCode);
    }

    public void confirmAuthCode(final ConfirmAuthCodeForResetPasswordCommand command) {
        final Member member = memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId());
        verifyAuthCode(member, command.authCode());
    }

    private void verifyAuthCode(final Member member, final String authCode) {
        final String key = generateAuthKey(member.getEmail());
        mailAuthenticationProcessor.verifyAuthCode(key, authCode);
        mailAuthenticationProcessor.deleteAuthCode(key); // 인증 성공 후 바로 제거 (재활용 X)
    }

    private String generateAuthKey(final Email email) {
        return AuthKey.PASSWORD_AUTH_KEY.generateAuthKey(email.getValue());
    }

    @AnotherArtWritableTransactional
    public void resetPassword(final ResetPasswordCommand command) {
        final Member member = memberRepository.getByNameAndEmailAndLoginId(command.name(), command.email(), command.loginId());
        member.updatePassword(command.password(), encryptor);
    }
}

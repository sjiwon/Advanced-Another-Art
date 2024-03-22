package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.auth.application.adapter.MailAuthenticationProcessor;
import com.sjiwon.anotherart.auth.domain.AuthKey;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.mail.application.adapter.EmailSender;
import com.sjiwon.anotherart.member.application.usecase.command.AuthForRetrieveLoginIdCommand;
import com.sjiwon.anotherart.member.application.usecase.command.ConfirmAuthCodeForLoginIdCommand;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class RetrieveLoginIdUseCase {
    private final MemberReader memberReader;
    private final MailAuthenticationProcessor mailAuthenticationProcessor;
    private final EmailSender emailSender;

    public void provideAuthCode(final AuthForRetrieveLoginIdCommand command) {
        final Member member = memberReader.getByNameAndEmail(command.name(), command.email());

        final String key = generateAuthKey(member.getEmail());
        final String authCode = mailAuthenticationProcessor.storeAuthCode(key);
        emailSender.sendAuthCodeForLoginId(member.getEmail().getValue(), authCode);
    }

    public String getLoginId(final ConfirmAuthCodeForLoginIdCommand command) {
        final Member member = memberReader.getByNameAndEmail(command.name(), command.email());
        verifyAuthCode(member, command.authCode());
        return member.getLoginId();
    }

    private void verifyAuthCode(final Member member, final String authCode) {
        final String key = generateAuthKey(member.getEmail());
        mailAuthenticationProcessor.verifyAuthCode(key, authCode);
        mailAuthenticationProcessor.deleteAuthCode(key); // 인증 성공 후 바로 제거 (재활용 X)
    }

    private String generateAuthKey(final Email email) {
        return AuthKey.LOGIN_AUTH_KEY.generateAuthKey(email.getValue());
    }
}

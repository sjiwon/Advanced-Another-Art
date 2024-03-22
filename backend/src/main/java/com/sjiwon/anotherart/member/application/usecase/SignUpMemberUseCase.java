package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Password;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.member.domain.service.MemberWriter;
import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_EMAIL;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_LOGIN_ID;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_NICKNAME;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_PHONE;

@UseCase
@RequiredArgsConstructor
public class SignUpMemberUseCase {
    private final MemberReader memberReader;
    private final Encryptor encryptor;
    private final MemberWriter memberWriter;

    public Long invoke(final SignUpMemberCommand command) {
        validateDuplicateResource(command.loginId(), command.email(), command.nickname(), command.phone());
        final Member member = createMember(command);
        return memberWriter.save(member).getId();
    }

    public void validateDuplicateResource(
            final String loginId,
            final Email email,
            final Nickname nickname,
            final Phone phone
    ) {
        if (memberReader.isNotUniqueLoginId(loginId)) {
            throw new MemberException(DUPLICATE_LOGIN_ID);
        }
        if (memberReader.isNotUniqueEmail(email)) {
            throw new MemberException(DUPLICATE_EMAIL);
        }
        if (memberReader.isNotUniqueNickname(nickname)) {
            throw new MemberException(DUPLICATE_NICKNAME);
        }
        if (memberReader.isNotUniquePhone(phone)) {
            throw new MemberException(DUPLICATE_PHONE);
        }
    }

    private Member createMember(final SignUpMemberCommand command) {
        return Member.createMember(
                command.name(),
                command.nickname(),
                command.loginId(),
                Password.encrypt(command.password(), encryptor),
                command.school(),
                command.phone(),
                command.email(),
                command.address()
        );
    }
}

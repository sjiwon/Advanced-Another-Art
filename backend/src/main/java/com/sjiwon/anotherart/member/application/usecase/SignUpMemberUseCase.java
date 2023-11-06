package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.application.usecase.command.SignUpMemberCommand;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Password;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpMemberUseCase {
    private final PasswordEncryptor passwordEncryptor;
    private final MemberRepository memberRepository;

    public Long invoke(final SignUpMemberCommand command) {
        validateLoginIdIsUnique(command.loginId());
        validateEmailIsUnique(command.email());
        validateNicknameIsUnique(command.nickname());
        validatePhoneIsUnique(command.phone());

        final Member member = build(command);
        return memberRepository.save(member).getId();
    }

    private void validateLoginIdIsUnique(final String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    private void validateEmailIsUnique(final Email email) {
        if (memberRepository.existsByEmailValue(email.getValue())) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validateNicknameIsUnique(final Nickname nickname) {
        if (memberRepository.existsByNicknameValue(nickname.getValue())) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validatePhoneIsUnique(final Phone phone) {
        if (memberRepository.existsByPhoneValue(phone.getValue())) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    private Member build(final SignUpMemberCommand command) {
        return Member.createMember(
                command.name(),
                command.nickname(),
                command.loginId(),
                Password.encrypt(command.password(), passwordEncryptor),
                command.school(),
                command.phone(),
                command.email(),
                command.address()
        );
    }
}

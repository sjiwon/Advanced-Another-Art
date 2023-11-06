package com.sjiwon.anotherart.member.domain.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberResourceValidator {
    private final MemberRepository memberRepository;

    public void validateInSignUp(
            final String loginId,
            final Email email,
            final Nickname nickname,
            final Phone phone
    ) {
        validateLoginIdIsUnique(loginId);
        validateEmailIsUnique(email.getValue());
        validateNicknameIsUnique(nickname.getValue());
        validatePhoneIsUnique(phone.getValue());
    }

    public void validateLoginIdIsUnique(final String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    public void validateEmailIsUnique(final String email) {
        if (memberRepository.existsByEmailValue(email)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }

    public void validateNicknameIsUnique(final String nickname) {
        if (memberRepository.existsByNicknameValue(nickname)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(final String phone) {
        if (memberRepository.existsByPhoneValue(phone)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }
}

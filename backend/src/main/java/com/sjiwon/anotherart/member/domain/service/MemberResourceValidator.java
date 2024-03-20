package com.sjiwon.anotherart.member.domain.service;

import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_EMAIL;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_LOGIN_ID;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_NICKNAME;
import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_PHONE;

@Service
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
            throw new MemberException(DUPLICATE_LOGIN_ID);
        }
    }

    public void validateEmailIsUnique(final String email) {
        if (memberRepository.existsByEmailValue(email)) {
            throw new MemberException(DUPLICATE_EMAIL);
        }
    }

    public void validateNicknameIsUnique(final String nickname) {
        if (memberRepository.existsByNicknameValue(nickname)) {
            throw new MemberException(DUPLICATE_NICKNAME);
        }
    }

    public void validatePhoneIsUnique(final String phone) {
        if (memberRepository.existsByPhoneValue(phone)) {
            throw new MemberException(DUPLICATE_PHONE);
        }
    }

    public void validateNicknameIsInUseByOther(final Long memberId, final String nickname) {
        if (memberRepository.isNicknameUsedByOther(memberId, nickname)) {
            throw new MemberException(DUPLICATE_NICKNAME);
        }
    }
}

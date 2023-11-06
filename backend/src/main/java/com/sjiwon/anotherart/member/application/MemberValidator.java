package com.sjiwon.anotherart.member.application;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public void validateNickname(final Nickname nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateNicknameForModify(final Long memberId, final Nickname nickname) {
        if (memberRepository.existsByIdNotAndNickname(memberId, nickname)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateLoginId(final String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    public void validatePhone(final String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    public void validateEmail(final Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}

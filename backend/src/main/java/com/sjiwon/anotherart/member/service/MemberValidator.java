package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.Nickname;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public void validateNickname(Nickname nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateNicknameForModify(Long memberId, Nickname nickname) {
        if (memberRepository.existsByIdNotAndNickname(memberId, nickname)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    public void validatePhone(String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    public void validateEmail(Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}

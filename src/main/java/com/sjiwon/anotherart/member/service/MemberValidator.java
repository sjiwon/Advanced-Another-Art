package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public void validateDuplicateResources(Member member) {
        validateDuplicateNickname(member.getNickname());
        validateDuplicateLoginId(member.getLoginId());
        validateDuplicatePhone(member.getPhone());
        validateDuplicateEmail(member.getEmail());
    }

    public void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    public void validateDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_LOGIN_ID);
        }
    }

    public void validateDuplicatePhone(String phone) {
        if (memberRepository.existsByPhone(phone)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_PHONE);
        }
    }

    public void validateDuplicateEmail(Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw AnotherArtException.type(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}

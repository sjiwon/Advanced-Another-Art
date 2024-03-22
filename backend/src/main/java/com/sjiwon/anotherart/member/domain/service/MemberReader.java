package com.sjiwon.anotherart.member.domain.service;

import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberReader {
    private final MemberRepository memberRepository;

    public Member getById(final long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    public Member getByNameAndEmail(final String name, final String email) {
        return memberRepository.findByNameAndEmail(name, email)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    public Member getByNameAndEmailAndLoginId(final String name, final String email, final String loginId) {
        return memberRepository.findByNameAndEmailAndLoginId(name, email, loginId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
    }

    public boolean isNotUniqueNickname(final Nickname nickname) {
        return memberRepository.existsByNicknameValue(nickname.getValue());
    }

    public boolean isNotUniqueLoginId(final String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    public boolean isNotUniquePhone(final Phone phone) {
        return memberRepository.existsByPhoneValue(phone.getValue());
    }

    public boolean isNotUniqueEmail(final Email email) {
        return memberRepository.existsByEmailValue(email.getValue());
    }

    public boolean isNicknameUsedByOther(final long memberId, final Nickname nickname) {
        final Long nicknameUsedId = memberRepository.findIdByNickname(nickname.getValue());
        return nicknameUsedId != null && !nicknameUsedId.equals(memberId);
    }
}

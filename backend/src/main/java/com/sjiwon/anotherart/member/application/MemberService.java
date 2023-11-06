package com.sjiwon.anotherart.member.application;

import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional
    public Long signUp(final Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(final Member member) {
        memberValidator.validateNickname(member.getNickname().getValue());
        memberValidator.validateLoginId(member.getLoginId());
        memberValidator.validatePhone(member.getPhone().getValue());
        memberValidator.validateEmail(member.getEmail().getValue());
    }

    public void duplicateCheck(final String resource, final String value) {
        switch (resource) {
            case "nickname" -> memberValidator.validateNickname(value);
            case "loginId" -> memberValidator.validateLoginId(value);
            case "phone" -> memberValidator.validatePhone(value);
            default -> memberValidator.validateEmail(value);
        }
    }

    @Transactional
    public void changeNickname(final Long memberId, final String value) {
        memberValidator.validateNicknameForModify(memberId, value);

        final Member member = memberRepository.getById(memberId);
        member.changeNickname(value);
    }

    @Transactional
    public void changeAddress(final Long memberId, final Integer postcode, final String defaultAddress, final String detailAddress) {
        final Member member = memberRepository.getById(memberId);
        member.changeAddress(postcode, defaultAddress, detailAddress);
    }

    public String findLoginId(final String name, final Email email) {
        return memberRepository.getByNameAndEmail(name, email.getValue())
                .getLoginId();
    }

    public void authForResetPassword(final String name, final Email email, final String loginId) {
        if (!memberRepository.existsByNameAndEmailValueAndLoginId(name, email.getValue(), loginId)) {
            throw AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Transactional
    public void resetPassword(final String loginId, final String changePassword) {
        final Member member = memberRepository.getByLoginId(loginId);
        member.changePassword(changePassword, passwordEncryptor);
    }

    @Transactional
    public void changePassword(final Long memberId, final String changePassword) {
        final Member member = memberRepository.getById(memberId);
        member.changePassword(changePassword, passwordEncryptor);
    }
}

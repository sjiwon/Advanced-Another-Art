package com.sjiwon.anotherart.member.application;

import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
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
    private final MemberFindService memberFindService;
    private final PasswordEncryptor passwordEncryptor;

    @Transactional
    public Long signUp(final Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(final Member member) {
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validateLoginId(member.getLoginId());
        memberValidator.validatePhone(member.getPhone().getValue());
        memberValidator.validateEmail(member.getEmail());
    }

    public void duplicateCheck(final String resource, final String value) {
        switch (resource) {
            case "nickname" -> memberValidator.validateNickname(Nickname.from(value));
            case "loginId" -> memberValidator.validateLoginId(value);
            case "phone" -> memberValidator.validatePhone(value);
            default -> memberValidator.validateEmail(Email.from(value));
        }
    }

    @Transactional
    public void changeNickname(final Long memberId, final String value) {
        memberValidator.validateNicknameForModify(memberId, Nickname.from(value));

        final Member member = memberFindService.findById(memberId);
        member.changeNickname(value);
    }

    @Transactional
    public void changeAddress(final Long memberId, final Integer postcode, final String defaultAddress, final String detailAddress) {
        final Member member = memberFindService.findById(memberId);
        member.changeAddress(postcode, defaultAddress, detailAddress);
    }

    public String findLoginId(final String name, final Email email) {
        return memberFindService.findByNameAndEmail(name, email)
                .getLoginId();
    }

    public void authForResetPassword(final String name, final Email email, final String loginId) {
        if (!memberRepository.existsByNameAndEmailAndLoginId(name, email, loginId)) {
            throw AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Transactional
    public void resetPassword(final String loginId, final String changePassword) {
        final Member member = memberFindService.findByLoginId(loginId);
        member.changePassword(changePassword, passwordEncryptor);
    }

    @Transactional
    public void changePassword(final Long memberId, final String changePassword) {
        final Member member = memberFindService.findById(memberId);
        member.changePassword(changePassword, passwordEncryptor);
    }
}

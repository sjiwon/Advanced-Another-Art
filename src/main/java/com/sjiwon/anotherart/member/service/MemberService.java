package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;
    private final PointDetailRepository pointDetailRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(Member member) {
        memberValidator.validateDuplicateResources(member);
        Member savedMember = memberRepository.save(member);
        pointDetailRepository.save(PointDetail.createPointDetail(savedMember));

        return savedMember.getId();
    }

    public void duplicateCheck(String resource, String value) {
        switch (resource) {
            case "nickname" -> memberValidator.validateDuplicateNickname(value);
            case "loginId" -> memberValidator.validateDuplicateLoginId(value);
            case "phone" -> memberValidator.validateDuplicatePhone(value);
            case "email" -> memberValidator.validateDuplicateEmail(Email.from(value));
            default -> throw AnotherArtException.type(GlobalErrorCode.VALIDATION_ERROR);
        }
    }

    @Transactional
    public void changeNickname(Long memberId, String changeNickname) {
        Member member = getMemberById(memberId);
        validateNicknameSameAsBefore(member, changeNickname); // 이전과 동일한 닉네임인지
        memberValidator.validateDuplicateNickname(changeNickname); // 다른 사람이 사용하고 있는 닉네임인지
        member.changeNickname(changeNickname);
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateNicknameSameAsBefore(Member member, String changeNickname) {
        if (member.isSameNickname(changeNickname)) {
            throw AnotherArtException.type(MemberErrorCode.NICKNAME_SAME_AS_BEFORE);
        }
    }

    public String findLoginId(String name, Email email) {
        Member member = getMemberByNameAndEmail(name, email);
        return member.getLoginId();
    }

    private Member getMemberByNameAndEmail(String name, Email email) {
        return memberRepository.findByNameAndEmail(name, email)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public void authMemberForResetPassword(String name, String loginId, Email email) {
        if (!memberRepository.existsByNameAndLoginIdAndEmail(name, loginId, email)) {
            throw AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Transactional
    public void resetPassword(String loginId, String changePassword) {
        Member member = getMemberByLoginId(loginId);
        member.changePassword(changePassword, passwordEncoder);
    }

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}

package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.Nickname;
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

    @Transactional
    public Long signUp(Member member) {
        validateUniqueFields(member);
        return memberRepository.save(member).getId();
    }

    private void validateUniqueFields(Member member) {
        memberValidator.validateNickname(member.getNickname());
        memberValidator.validateLoginId(member.getLoginId());
        memberValidator.validatePhone(member.getPhone());
        memberValidator.validateEmail(member.getEmail());
    }

    public void duplicateCheck(String resource, String value) {
        switch (resource) {
            case "nickname" -> memberValidator.validateNickname(Nickname.from(value));
            case "loginId" -> memberValidator.validateLoginId(value);
            case "phone" -> memberValidator.validatePhone(value);
            default -> memberValidator.validateEmail(Email.from(value));
        }
    }

    @Transactional
    public void changeNickname(Long memberId, String value) {
        memberValidator.validateNicknameForModify(memberId, Nickname.from(value));

        Member member = memberFindService.findById(memberId);
        member.changeNickname(value);
    }

    @Transactional
    public void changeAddress(Long memberId, Integer postcode, String defaultAddress, String detailAddress) {
        Member member = memberFindService.findById(memberId);
        member.changeAddress(postcode, defaultAddress, detailAddress);
    }
}

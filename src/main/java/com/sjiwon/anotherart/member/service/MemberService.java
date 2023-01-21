package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.point.domain.PointDetail;
import com.sjiwon.anotherart.point.domain.PointDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;
    private final PointDetailRepository pointDetailRepository;

    @Transactional
    public Long signUp(Member member) {
        memberValidator.validateDuplicateResources(member);
        Member savedMember = memberRepository.save(member);
        pointDetailRepository.save(PointDetail.createPointDetail(savedMember));

        return savedMember.getId();
    }
}

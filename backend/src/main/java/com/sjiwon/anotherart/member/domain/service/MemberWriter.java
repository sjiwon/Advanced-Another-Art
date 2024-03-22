package com.sjiwon.anotherart.member.domain.service;

import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberWriter {
    private final MemberRepository memberRepository;

    public Member save(final Member target) {
        return memberRepository.save(target);
    }
}

package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.infra.query.dto.response.UserPointHistory;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberProfileService {
    private final MemberRepository memberRepository;
    private final MemberFindService memberFindService;

    public UserProfile getUserProfile(Long memberId) {
        Member member = memberFindService.findById(memberId);
        Integer totalPoint = memberRepository.getTotalPointByMemberId(memberId);

        return UserProfile.builder()
                .member(member)
                .totalPoint(totalPoint)
                .build();
    }

    public List<UserPointHistory> getUserPointHistory(Long memberId) {
        return memberRepository.findUserPointHistoryByMemberId(memberId);
    }
}

package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberPointService {
    private final MemberRepository memberRepository;
    private final PointDetailRepository pointDetailRepository;

    @Transactional
    public void chargePoint(Long memberId, int chargeAmount) {
        Member member = getMemberById(memberId);
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
    }

    public void refundPoint(Long memberId, int refundAmount) {
        Member member = getMemberById(memberId);
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.REFUND, refundAmount));
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}

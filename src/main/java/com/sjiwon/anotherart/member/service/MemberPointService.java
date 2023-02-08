package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberPointService {
    private final MemberFindService memberFindService;

    @Transactional
    public void chargePoint(Long memberId, int chargeAmount) {
        Member member = memberFindService.findById(memberId);
        member.addPointDetail(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
    }

    @Transactional
    public void refundPoint(Long memberId, int refundAmount) {
        Member member = memberFindService.findById(memberId);
        member.addPointDetail(PointDetail.insertPointDetail(member, PointType.REFUND, refundAmount));
    }
}

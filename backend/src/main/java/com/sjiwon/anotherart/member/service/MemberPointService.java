package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static com.sjiwon.anotherart.member.domain.point.PointType.REFUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberPointService {
    private final MemberFindService memberFindService;

    public void chargePoint(Long memberId, Integer amount) {
        Member member = memberFindService.findById(memberId);
        member.addPointRecords(CHARGE, amount);
    }

    public void refundPoint(Long memberId, Integer amount) {
        Member member = memberFindService.findById(memberId);
        member.addPointRecords(REFUND, amount);
    }
}

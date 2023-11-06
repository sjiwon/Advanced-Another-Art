package com.sjiwon.anotherart.member.application;

import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sjiwon.anotherart.member.domain.model.PointType.CHARGE;
import static com.sjiwon.anotherart.member.domain.model.PointType.REFUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberPointService {
    private final MemberFindService memberFindService;

    public void chargePoint(final Long memberId, final Integer amount) {
        final Member member = memberFindService.findById(memberId);
        member.addPointRecords(CHARGE, amount);
    }

    public void refundPoint(final Long memberId, final Integer amount) {
        final Member member = memberFindService.findById(memberId);
        member.addPointRecords(REFUND, amount);
    }
}

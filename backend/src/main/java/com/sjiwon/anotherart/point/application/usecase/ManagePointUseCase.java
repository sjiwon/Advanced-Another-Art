package com.sjiwon.anotherart.point.application.usecase;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.point.application.usecase.command.ChargePointCommand;
import com.sjiwon.anotherart.point.application.usecase.command.RefundPointCommand;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.point.domain.model.PointType.CHARGE;
import static com.sjiwon.anotherart.point.domain.model.PointType.REFUND;

@Service
@RequiredArgsConstructor
public class ManagePointUseCase {
    private final MemberRepository memberRepository;
    private final PointRecordRepository pointRecordRepository;

    @AnotherArtWritableTransactional
    public void charge(final ChargePointCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        final PointRecord chargeRecord = PointRecord.addPointRecord(member, CHARGE, command.chargeAmount());
        pointRecordRepository.save(chargeRecord);
    }

    @AnotherArtWritableTransactional
    public void refund(final RefundPointCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        final PointRecord refundRecord = PointRecord.addPointRecord(member, REFUND, command.refundAmount());
        pointRecordRepository.save(refundRecord);
    }
}

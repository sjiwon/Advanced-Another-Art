package com.sjiwon.anotherart.point.application.usecase;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.point.application.usecase.command.ChargePointCommand;
import com.sjiwon.anotherart.point.application.usecase.command.RefundPointCommand;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.service.PointRecordWriter;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ManagePointUseCase {
    private final MemberReader memberReader;
    private final PointRecordWriter pointRecordWriter;

    @AnotherArtWritableTransactional
    public void charge(final ChargePointCommand command) {
        final Member member = memberReader.getById(command.memberId());
        member.increaseTotalPoint(command.chargeAmount());
        pointRecordWriter.save(PointRecord.addChargeRecord(member, command.chargeAmount()));
    }

    @AnotherArtWritableTransactional
    public void refund(final RefundPointCommand command) {
        final Member member = memberReader.getById(command.memberId());
        member.decreaseTotalPoint(command.refundAmount());
        pointRecordWriter.save(PointRecord.addRefundRecord(member, command.refundAmount()));
    }
}

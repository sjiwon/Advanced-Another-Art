package com.sjiwon.anotherart.point.application.usecase;

import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.point.application.usecase.command.ChargePointCommand;
import com.sjiwon.anotherart.point.application.usecase.command.RefundPointCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagePointUseCase {
    private final MemberRepository memberRepository;

    public void charge(final ChargePointCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        // TODO charge
    }

    public void refund(final RefundPointCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        // TODO refund
    }
}

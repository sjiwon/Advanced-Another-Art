package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateResourceUseCase {
    private final MemberResourceValidator memberResourceValidator;
    private final MemberRepository memberRepository;

    @AnotherArtWritableTransactional
    public void invoke(final UpdateNicknameCommand command) {
        memberResourceValidator.validateNicknameIsInUseByOther(command.memberId(), command.nickname());

        final Member member = memberRepository.getById(command.memberId());
        member.updateNickname(command.nickname());
    }

    @AnotherArtWritableTransactional
    public void invoke(final UpdateAddressCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        member.updateAddress(command.postcode(), command.defaultAddress(), command.detailAddress());
    }
}

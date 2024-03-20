package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.encrypt.PasswordEncryptor;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateMemberResourceUseCase {
    private final MemberResourceValidator memberResourceValidator;
    private final PasswordEncryptor passwordEncryptor;
    private final MemberRepository memberRepository;

    @AnotherArtWritableTransactional
    public void updateNickname(final UpdateNicknameCommand command) {
        memberResourceValidator.validateNicknameIsInUseByOther(command.memberId(), command.nickname());

        final Member member = memberRepository.getById(command.memberId());
        member.updateNickname(command.nickname());
    }

    @AnotherArtWritableTransactional
    public void updateAddress(final UpdateAddressCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        member.updateAddress(command.postcode(), command.defaultAddress(), command.detailAddress());
    }

    @AnotherArtWritableTransactional
    public void updatePassword(final UpdatePasswordCommand command) {
        final Member member = memberRepository.getById(command.memberId());
        member.updatePassword(command.password(), passwordEncryptor);
    }
}

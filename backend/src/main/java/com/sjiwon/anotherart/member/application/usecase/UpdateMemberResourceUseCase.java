package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.utils.encrypt.Encryptor;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateAddressCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdateNicknameCommand;
import com.sjiwon.anotherart.member.application.usecase.command.UpdatePasswordCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.member.exception.MemberException;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.member.exception.MemberExceptionCode.DUPLICATE_NICKNAME;

@UseCase
@RequiredArgsConstructor
public class UpdateMemberResourceUseCase {
    private final MemberReader memberReader;
    private final Encryptor encryptor;

    @AnotherArtWritableTransactional
    public void updateNickname(final UpdateNicknameCommand command) {
        if (memberReader.isNicknameUsedByOther(command.memberId(), Nickname.from(command.nickname()))) {
            throw new MemberException(DUPLICATE_NICKNAME);
        }

        final Member member = memberReader.getById(command.memberId());
        member.updateNickname(command.nickname());
    }

    @AnotherArtWritableTransactional
    public void updateAddress(final UpdateAddressCommand command) {
        final Member member = memberReader.getById(command.memberId());
        member.updateAddress(command.postcode(), command.defaultAddress(), command.detailAddress());
    }

    @AnotherArtWritableTransactional
    public void updatePassword(final UpdatePasswordCommand command) {
        final Member member = memberReader.getById(command.memberId());
        member.updatePassword(command.password(), encryptor);
    }
}

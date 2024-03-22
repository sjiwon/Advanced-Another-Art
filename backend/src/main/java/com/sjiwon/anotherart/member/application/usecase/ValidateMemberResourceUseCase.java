package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.application.usecase.command.ValidateMemberResourceCommand;
import com.sjiwon.anotherart.member.domain.model.Email;
import com.sjiwon.anotherart.member.domain.model.Nickname;
import com.sjiwon.anotherart.member.domain.model.Phone;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ValidateMemberResourceUseCase {
    private final MemberReader memberReader;

    public boolean useable(final ValidateMemberResourceCommand command) {
        return switch (command.resource()) {
            case LOGIN_ID -> !memberReader.isNotUniqueLoginId(command.value());
            case EMAIL -> !memberReader.isNotUniqueEmail(Email.from(command.value()));
            case NICKNAME -> !memberReader.isNotUniqueNickname(Nickname.from(command.value()));
            case PHONE -> !memberReader.isNotUniquePhone(Phone.from(command.value()));
        };
    }
}

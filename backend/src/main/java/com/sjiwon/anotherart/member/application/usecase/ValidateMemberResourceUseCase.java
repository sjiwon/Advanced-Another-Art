package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.application.usecase.command.ValidateMemberResourceCommand;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ValidateMemberResourceUseCase {
    private final MemberResourceValidator memberResourceValidator;

    public void invoke(final ValidateMemberResourceCommand command) {
        switch (command.resource()) {
            case LOGIN_ID -> memberResourceValidator.validateLoginIdIsUnique(command.value());
            case EMAIL -> memberResourceValidator.validateEmailIsUnique(command.value());
            case NICKNAME -> memberResourceValidator.validateNicknameIsUnique(command.value());
            case PHONE -> memberResourceValidator.validatePhoneIsUnique(command.value());
            default -> throw new IllegalArgumentException();
        }
    }
}

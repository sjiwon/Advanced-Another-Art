package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.member.application.usecase.command.ValidateResouceCommand;
import com.sjiwon.anotherart.member.domain.service.MemberResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateResourceUseCase {
    private final MemberResourceValidator memberResourceValidator;

    public void invoke(final ValidateResouceCommand command) {
        switch (command.resource()) {
            case LOGIN_ID -> memberResourceValidator.validateLoginIdIsUnique(command.value());
            case EMAIL -> memberResourceValidator.validateEmailIsUnique(command.value());
            case NICKNAME -> memberResourceValidator.validateNicknameIsUnique(command.value());
            case PHONE -> memberResourceValidator.validatePhoneIsUnique(command.value());
            default -> throw new IllegalArgumentException();
        }
    }
}

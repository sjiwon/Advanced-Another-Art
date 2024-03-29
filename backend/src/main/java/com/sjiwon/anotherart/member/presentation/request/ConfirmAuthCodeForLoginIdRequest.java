package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.ConfirmAuthCodeForLoginIdCommand;
import jakarta.validation.constraints.NotBlank;

public record ConfirmAuthCodeForLoginIdRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @NotBlank(message = "사용자 이메일은 필수입니다.")
        String email,

        @NotBlank(message = "인증 번호는 필수입니다.")
        String authCode
) {
    public ConfirmAuthCodeForLoginIdCommand toCommand() {
        return new ConfirmAuthCodeForLoginIdCommand(
                name,
                email,
                authCode
        );
    }
}

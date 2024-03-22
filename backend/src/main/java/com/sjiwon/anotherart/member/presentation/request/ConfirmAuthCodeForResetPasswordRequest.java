package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.ConfirmAuthCodeForResetPasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record ConfirmAuthCodeForResetPasswordRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @NotBlank(message = "사용자 이메일은 필수입니다.")
        String email,

        @NotBlank(message = "사용자 로그인 아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "인증 번호는 필수입니다.")
        String authCode
) {
    public ConfirmAuthCodeForResetPasswordCommand toCommand() {
        return new ConfirmAuthCodeForResetPasswordCommand(
                name,
                email,
                loginId,
                authCode
        );
    }
}

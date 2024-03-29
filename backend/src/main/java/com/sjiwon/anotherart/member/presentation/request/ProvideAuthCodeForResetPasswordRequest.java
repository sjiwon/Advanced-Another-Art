package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.AuthForResetPasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record ProvideAuthCodeForResetPasswordRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @NotBlank(message = "사용자 이메일은 필수입니다.")
        String email,

        @NotBlank(message = "사용자 로그인 아이디는 필수입니다.")
        String loginId
) {
    public AuthForResetPasswordCommand toCommand() {
        return new AuthForResetPasswordCommand(
                name,
                email,
                loginId
        );
    }
}

package com.sjiwon.anotherart.member.presentation.request;

import com.sjiwon.anotherart.member.application.usecase.command.ResetPasswordCommand;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @NotBlank(message = "사용자 이메일은 필수입니다.")
        String email,

        @NotBlank(message = "사용자 로그인 아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "변경할 비밀번호는 필수입니다.")
        String password
) {
    public ResetPasswordCommand toCommand() {
        return new ResetPasswordCommand(
                name,
                email,
                loginId,
                password
        );
    }
}

package com.sjiwon.anotherart.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthForResetPasswordRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId
) {
}

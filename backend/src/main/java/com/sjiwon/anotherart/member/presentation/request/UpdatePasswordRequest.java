package com.sjiwon.anotherart.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
        @NotBlank(message = "변경할 비밀번호는 필수입니다.")
        String value
) {
}

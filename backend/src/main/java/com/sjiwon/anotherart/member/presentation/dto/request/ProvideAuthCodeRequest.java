package com.sjiwon.anotherart.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProvideAuthCodeRequest(
        @NotBlank(message = "사용자 이름은 필수입니다.")
        String name,

        @NotBlank(message = "사용자 이메일은 필수입니다.")
        String email
) {
}

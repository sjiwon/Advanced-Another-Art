package com.sjiwon.anotherart.member.controller.dto.request;

import jakarta.validation.constraints.NotBlank;

public record FindLoginIdRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
}

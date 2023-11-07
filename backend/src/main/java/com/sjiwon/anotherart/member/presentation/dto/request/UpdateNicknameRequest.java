package com.sjiwon.anotherart.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(
        @NotBlank(message = "변경할 닉네임은 필수입니다.")
        String value
) {
}

package com.sjiwon.anotherart.member.presentation.request;

import jakarta.validation.constraints.NotBlank;

public record MemberDuplicateCheckRequest(
        @NotBlank(message = "중복 체크 값은 필수입니다.")
        String value
) {
}

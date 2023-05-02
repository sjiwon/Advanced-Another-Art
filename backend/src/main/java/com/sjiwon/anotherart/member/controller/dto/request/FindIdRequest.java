package com.sjiwon.anotherart.member.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record FindIdRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name,

        @NotBlank(message = "이메일은 필수입니다.")
        String email
) {
    @Builder
    public FindIdRequest {}
}

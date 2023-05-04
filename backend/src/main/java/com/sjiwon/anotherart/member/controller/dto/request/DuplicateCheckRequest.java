package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.global.annotation.validation.ValidDuplicateResource;

import javax.validation.constraints.NotBlank;

public record DuplicateCheckRequest(
        @ValidDuplicateResource
        @NotBlank(message = "중복 체크 타입은 필수입니다.")
        String resource,

        @NotBlank(message = "중복 체크 값은 필수입니다.")
        String value
) {
}

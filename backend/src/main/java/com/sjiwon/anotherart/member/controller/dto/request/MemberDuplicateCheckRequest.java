package com.sjiwon.anotherart.member.controller.dto.request;

import com.sjiwon.anotherart.member.utils.validator.ValidMemberDuplicateResource;

import javax.validation.constraints.NotBlank;

public record MemberDuplicateCheckRequest(
        @ValidMemberDuplicateResource
        @NotBlank(message = "중복 체크 타입은 필수입니다.")
        String resource,

        @NotBlank(message = "중복 체크 값은 필수입니다.")
        String value
) {
}

package com.sjiwon.anotherart.member.controller.dto.request;

import javax.validation.constraints.NotBlank;

public record DuplicateCheckRequest(
        @NotBlank(message = "중복 체크 타입[nickname / loginId / phone / email]은 필수입니다.")
        String resource,

        @NotBlank(message = "중복 체크 값은 필수입니다.")
        String value
) {
}

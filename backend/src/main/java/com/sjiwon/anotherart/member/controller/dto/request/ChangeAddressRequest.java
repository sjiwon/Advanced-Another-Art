package com.sjiwon.anotherart.member.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeAddressRequest(
        @NotNull(message = "우편번호는 필수입니다.")
        Integer postcode,

        @NotBlank(message = "기본 주소는 필수입니다.")
        String defaultAddress,

        @NotBlank(message = "상세 주소는 필수입니다.")
        String detailAddress
) {
}

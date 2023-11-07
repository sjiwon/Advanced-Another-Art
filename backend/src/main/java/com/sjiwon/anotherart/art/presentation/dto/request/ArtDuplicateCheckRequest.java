package com.sjiwon.anotherart.art.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ArtDuplicateCheckRequest(
        @NotBlank(message = "중복 체크 값은 필수입니다.")
        String value
) {
}

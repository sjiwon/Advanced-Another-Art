package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.utils.validator.ValidArtDuplicateResource;
import jakarta.validation.constraints.NotBlank;

public record ArtDuplicateCheckRequest(
        @ValidArtDuplicateResource
        @NotBlank(message = "중복 체크 타입은 필수입니다.")
        String resource,

        @NotBlank(message = "중복 체크 값은 필수입니다.")
        String value
) {
}

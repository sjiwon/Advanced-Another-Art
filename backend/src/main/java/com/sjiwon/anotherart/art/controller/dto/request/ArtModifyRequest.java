package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.utils.validator.ValidHashtagCount;

import javax.validation.constraints.NotBlank;
import java.util.Set;

public record ArtModifyRequest(
        @NotBlank(message = "작품명은 필수입니다.")
        String name,

        @NotBlank(message = "작품 설명은 필수입니다.")
        String description,

        @ValidHashtagCount
        Set<String> hashtags
) {
}

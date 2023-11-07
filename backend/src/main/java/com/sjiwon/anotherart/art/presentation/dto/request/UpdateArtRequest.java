package com.sjiwon.anotherart.art.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record UpdateArtRequest(
        @NotBlank(message = "작품명은 필수입니다.")
        String name,

        @NotBlank(message = "작품 설명은 필수입니다.")
        String description,

        Set<String> hashtags
) {
}

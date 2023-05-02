package com.sjiwon.anotherart.art.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

public record ChangeArtDescriptionRequest(
        @NotBlank(message = "변경할 작품 설명은 필수입니다.")
        String changeDescription
) {
    @Builder
    public ChangeArtDescriptionRequest {}
}

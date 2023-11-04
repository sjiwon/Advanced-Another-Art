package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.utils.validator.ValidArtType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtHashtagSearchRequest(
        @NotBlank(message = "정렬 기준은 필수입니다.")
        String sortType,

        @NotNull(message = "현재 페이지 번호는 필수입니다.")
        @Min(message = "페이지는 1페이지부터 시작합니다.", value = 1)
        Integer page,

        @ValidArtType
        @NotBlank(message = "작품 타입은 필수입니다.")
        String artType,

        @NotBlank(message = "해시태그는 필수입니다.")
        String hashtag
) {
}

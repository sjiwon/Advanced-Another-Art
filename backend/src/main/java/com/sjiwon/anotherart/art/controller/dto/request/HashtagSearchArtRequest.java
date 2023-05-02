package com.sjiwon.anotherart.art.controller.dto.request;

import lombok.Builder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record HashtagSearchArtRequest(
        @NotBlank(message = "해시태그는 필수입니다.")
        String hashtag,

        @NotBlank(message = "작품 타입은 필수입니다.")
        String type,

        @NotBlank(message = "정렬 기준은 필수입니다.")
        String sort,

        @NotNull(message = "현재 페이지 번호는 필수입니다.")
        @Min(message = "페이지는 1페이지부터 시작합니다.", value = 1)
        Integer page
) {
    @Builder
    public HashtagSearchArtRequest {}

    public boolean isAuctionType() {
        return this.type.equalsIgnoreCase("auction");
    }
}

package com.sjiwon.anotherart.art.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class KeywordSearchArtRequest {
    @NotBlank(message = "검색 키워드는 필수입니다.")
    private String keyword;

    @NotBlank(message = "작품 타입은 필수입니다.")
    private String type;

    @NotBlank(message = "정렬 기준은 필수입니다.")
    private String sort;

    @NotNull(message = "현재 페이지 번호는 필수입니다.")
    @Min(message = "페이지는 1페이지부터 시작합니다.", value = 1)
    private Integer page;

    public boolean isAuctionType() {
        return this.type.equalsIgnoreCase("auction");
    }
}

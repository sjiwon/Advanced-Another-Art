package com.sjiwon.anotherart.art.presentation.request;

import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByKeyword;
import com.sjiwon.anotherart.art.domain.repository.query.spec.SortType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtKeywordSearchRequest(
        @NotBlank(message = "정렬 기준은 필수입니다.")
        String sortType,

        @NotNull(message = "현재 페이지 번호는 필수입니다.")
        @Min(message = "페이지는 1페이지부터 시작합니다.", value = 1)
        Integer page,

        @NotBlank(message = "키워드는 필수입니다.")
        String keyword
) {
    public GetArtsByKeyword toQuery() {
        return new GetArtsByKeyword(
                SortType.from(sortType),
                keyword,
                page
        );
    }
}

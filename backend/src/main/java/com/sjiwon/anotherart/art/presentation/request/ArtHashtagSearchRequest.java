package com.sjiwon.anotherart.art.presentation.request;

import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByHashtag;
import com.sjiwon.anotherart.art.domain.repository.query.spec.SortType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtHashtagSearchRequest(
        @NotBlank(message = "정렬 기준은 필수입니다.")
        String sortType,

        @NotNull(message = "현재 페이지 번호는 필수입니다.")
        @Min(message = "페이지는 1페이지부터 시작합니다.", value = 1)
        Integer page,

        @NotBlank(message = "해시태그는 필수입니다.")
        String hashtag
) {
    public GetArtsByHashtag toQuery() {
        return new GetArtsByHashtag(
                SortType.from(sortType),
                hashtag,
                page
        );
    }
}

package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class MainPageSearchRequest {
    @NotBlank(message = ArtRequestValidationMessage.CommonPage.SEARCH_SORT)
    private String sort;

    @NotNull(message = ArtRequestValidationMessage.CommonPage.SEARCH_PAGE)
    @Min(message = ArtRequestValidationMessage.CommonPage.SEARCH_PAGE_MIN, value = 1)
    private Integer page;
}

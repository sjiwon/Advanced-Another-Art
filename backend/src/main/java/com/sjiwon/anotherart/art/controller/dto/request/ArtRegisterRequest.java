package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.utils.validator.ValidArtType;
import com.sjiwon.anotherart.art.utils.validator.ValidAuctionStartDate;
import com.sjiwon.anotherart.art.utils.validator.ValidHashtagCount;
import com.sjiwon.anotherart.art.utils.validator.ValidImageContentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

public record ArtRegisterRequest(
        @NotBlank(message = "작품명은 필수입니다.")
        String name,

        @NotBlank(message = "작품 설명은 필수입니다.")
        String description,

        @ValidArtType
        @NotBlank(message = "작품 타입은 필수입니다.")
        String type,

        @NotNull(message = "작품 가격은 필수입니다.")
        @Min(message = "작품 가격은 최소 1000원 이상이여야 합니다.", value = 1000)
        int price,

        @ValidAuctionStartDate
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime auctionStartDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime auctionEndDate,

        @ValidHashtagCount
        Set<String> hashtags,

        @ValidImageContentType
        MultipartFile file
) {
}

package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.service.dto.request.ArtRegisterRequestDto;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.global.annotation.validation.ValidAuctionArtStartDate;
import com.sjiwon.anotherart.global.annotation.validation.ValidHashtagCount;
import com.sjiwon.anotherart.global.annotation.validation.ValidImageContentType;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

public record ArtRegisterRequest(
        @NotBlank(message = "작품 타입(경매/일반)은 필수입니다.")
        String artType,

        @NotBlank(message = "작품명은 필수입니다.")
        String name,

        @NotBlank(message = "작품 설명은 필수입니다.")
        String description,

        @NotNull(message = "작품 가격은 필수입니다.")
        @Min(message = "작품 가격은 최소 1000원 이상이여야 합니다.", value = 1000)
        Integer price,

        @ValidImageContentType
        MultipartFile file,

        @ValidHashtagCount
        List<String> hashtagList,

        @ValidAuctionArtStartDate
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime endDate
) {
    @Builder
    public ArtRegisterRequest {}

    public ArtRegisterRequestDto toGeneralArtDto() {
        return ArtRegisterRequestDto.builder()
                .artType(ArtType.GENERAL)
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .file(this.file)
                .hashtagList(new HashSet<>(this.hashtagList))
                .build();
    }

    public ArtRegisterRequestDto toAuctionArtDto() {
        return ArtRegisterRequestDto.builder()
                .artType(ArtType.AUCTION)
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .file(this.file)
                .period(Period.of(this.startDate, this.endDate))
                .hashtagList(new HashSet<>(this.hashtagList))
                .build();
    }

    public boolean isAuctionType() {
        return this.artType.equalsIgnoreCase("auction");
    }
}

package com.sjiwon.anotherart.art.controller.dto.request;

import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.exception.ArtRequestValidationMessage;
import com.sjiwon.anotherart.art.service.dto.request.ArtRegisterRequestDto;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.global.annotation.ValidArtHashtagSize;
import com.sjiwon.anotherart.global.annotation.ValidArtImage;
import com.sjiwon.anotherart.global.annotation.ValidAuctionArtStartDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Getter
public class ArtRegisterRequest {
    @NotBlank(message = ArtRequestValidationMessage.Registration.ART_TYPE)
    private final String artType;

    @NotBlank(message = ArtRequestValidationMessage.Registration.ART_NAME)
    private final String name;

    @NotBlank(message = ArtRequestValidationMessage.Registration.ART_DESCRIPTION)
    private final String description;

    @NotNull(message = ArtRequestValidationMessage.Registration.ART_PRICE)
    @Min(message = ArtRequestValidationMessage.Registration.ART_PRICE_MIN, value = 1000)
    private final Integer price;

    @ValidArtImage
    private final MultipartFile file;

    @ValidArtHashtagSize
    private final List<String> hashtagList;

    @ValidAuctionArtStartDate
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private final LocalDateTime endDate;

    @Builder
    public ArtRegisterRequest(String artType, String name, String description, Integer price, MultipartFile file, List<String> hashtagList, LocalDateTime startDate, LocalDateTime endDate) {
        this.artType = artType;
        this.name = name;
        this.description = description;
        this.price = price;
        this.file = file;
        this.hashtagList = hashtagList;
        this.startDate = startDate;
        this.endDate = endDate;
    }

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

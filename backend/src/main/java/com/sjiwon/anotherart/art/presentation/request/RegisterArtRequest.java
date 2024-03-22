package com.sjiwon.anotherart.art.presentation.request;

import com.sjiwon.anotherart.art.application.usecase.command.RegisterArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.art.utils.validator.ValidAuctionStartDate;
import com.sjiwon.anotherart.file.utils.converter.FileConverter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

public record RegisterArtRequest(
        @NotBlank(message = "작품명은 필수입니다.")
        String name,

        @NotBlank(message = "작품 설명은 필수입니다.")
        String description,

        @NotBlank(message = "작품 타입은 필수입니다.")
        String type,

        @NotNull(message = "작품 가격은 필수입니다.")
        @Min(message = "작품 가격은 최소 1000원 이상이여야 합니다.", value = 1000)
        Integer price,

        @ValidAuctionStartDate
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime auctionStartDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime auctionEndDate,

        Set<String> hashtags,

        MultipartFile file
) {
    public RegisterArtCommand toCommand(final long memberId) {
        return new RegisterArtCommand(
                memberId,
                ArtName.from(name),
                Description.from(description),
                Art.Type.from(type),
                price,
                auctionStartDate,
                auctionEndDate,
                hashtags,
                FileConverter.convertImageFile(file)
        );
    }
}

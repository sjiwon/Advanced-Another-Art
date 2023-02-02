package com.sjiwon.anotherart.art.service.dto.request;

import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.auction.domain.Period;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Getter
public class ArtRegisterRequestDto {
    private final ArtType artType;
    private final String name;
    private final String description;
    private final int price;
    private final MultipartFile file;
    private final Period period;
    private final Set<String> hashtagList;

    @Builder
    public ArtRegisterRequestDto(ArtType artType, String name, String description, int price, MultipartFile file, Period period, Set<String> hashtagList) {
        this.artType = artType;
        this.name = name;
        this.description = description;
        this.price = price;
        this.file = file;
        this.period = period;
        this.hashtagList = hashtagList;
    }
}

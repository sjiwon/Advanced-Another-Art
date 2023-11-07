package com.sjiwon.anotherart.art.application.usecase.command;

import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.art.domain.model.Description;
import com.sjiwon.anotherart.file.domain.model.RawFileData;

import java.time.LocalDateTime;
import java.util.Set;

public record RegisterArtCommand(
        Long ownerId,
        ArtName name,
        Description description,
        ArtType type,
        int price,
        LocalDateTime auctionStartDate,
        LocalDateTime auctionEndDate,
        Set<String> hashtags,
        RawFileData image
) {
}

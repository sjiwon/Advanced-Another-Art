package com.sjiwon.anotherart.art.application.usecase.command;

import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.model.Description;

import java.util.Set;

public record UpdateArtCommand(
        long memberId,
        long artId,
        ArtName name,
        Description description,
        Set<String> hashtags
) {
}

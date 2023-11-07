package com.sjiwon.anotherart.art.application.usecase.command;

import com.sjiwon.anotherart.art.domain.model.ArtDuplicateResource;

public record ValidateArtResourceCommand(
        ArtDuplicateResource resource,
        String value
) {
}

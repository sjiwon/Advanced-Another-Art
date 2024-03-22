package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.ValidateArtResourceCommand;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ValidateArtResourceUseCase {
    private final ArtReader artReader;

    public boolean useable(final ValidateArtResourceCommand command) {
        return switch (command.resource()) {
            case NAME -> !artReader.isNotUniqueName(ArtName.from(command.value()));
        };
    }
}

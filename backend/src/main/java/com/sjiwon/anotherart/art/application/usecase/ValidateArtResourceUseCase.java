package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.ValidateArtResourceCommand;
import com.sjiwon.anotherart.art.domain.service.ArtResourceValidator;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.art.domain.model.ArtDuplicateResource.NAME;

@UseCase
@RequiredArgsConstructor
public class ValidateArtResourceUseCase {
    private final ArtResourceValidator artResourceValidator;

    public void invoke(final ValidateArtResourceCommand command) {
        if (command.resource() == NAME) {
            artResourceValidator.validatenNameIsUnique(command.value());
            return;
        }

        throw new IllegalArgumentException();
    }
}

package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.ValidateArtResourceCommand;
import com.sjiwon.anotherart.art.domain.service.ArtResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.art.domain.model.ArtDuplicateResource.NAME;

@Service
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

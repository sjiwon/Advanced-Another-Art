package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.UpdateArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.service.ArtResourceValidator;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class UpdateArtUseCase {
    private final ArtResourceValidator artResourceValidator;
    private final ArtRepository artRepository;

    @AnotherArtWritableTransactional
    public void invoke(final UpdateArtCommand command) {
        artResourceValidator.validateNameIsInUseByOther(command.artId(), command.name().getValue());

        final Art art = artRepository.getById(command.artId());
        art.update(command.name(), command.description(), command.hashtags());
    }
}

package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.UpdateArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.DUPLICATE_NAME;

@UseCase
@RequiredArgsConstructor
public class UpdateArtUseCase {
    private final ArtReader artReader;

    @AnotherArtWritableTransactional
    public void invoke(final UpdateArtCommand command) {
        if (artReader.isNameUsedByOther(command.artId(), command.name())) {
            throw new ArtException(DUPLICATE_NAME);
        }

        final Art art = artReader.getByIdAndOwnerId(command.artId(), command.memberId());
        art.update(command.name(), command.description(), command.hashtags());
    }
}

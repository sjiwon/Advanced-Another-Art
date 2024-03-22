package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.DeleteArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtDeleter;
import com.sjiwon.anotherart.art.domain.service.ArtDeletionPreInspector;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DeleteArtUseCase {
    private final ArtReader artReader;
    private final ArtDeletionPreInspector artDeletionPreInspector;
    private final ArtDeleter artDeleter;

    public void invoke(final DeleteArtCommand command) {
        final Art art = artReader.getByIdAndOwnerId(command.artId(), command.memberId());
        artDeletionPreInspector.checkArtCanBeDeleted(art);
        artDeleter.execute(art);
    }
}

package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.DeleteArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.service.ArtDeleter;
import com.sjiwon.anotherart.art.domain.service.ArtDeletionPreInspector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteArtUseCase {
    private final ArtRepository artRepository;
    private final ArtDeletionPreInspector artDeletionPreInspector;
    private final ArtDeleter artDeleter;

    public void invoke(final DeleteArtCommand command) {
        final Art art = artRepository.getById(command.artId());
        artDeletionPreInspector.checkArtCanBeDeleted(art);
        artDeleter.execute(art);
    }
}

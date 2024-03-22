package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.ART_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ArtReader {
    private final ArtRepository artRepository;

    public Art getById(final long id) {
        return artRepository.findById(id)
                .orElseThrow(() -> new ArtException(ART_NOT_FOUND));
    }

    public Art getByIdAndOwnerId(final long id, final long ownerId) {
        return artRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ArtException(ART_NOT_FOUND));
    }

    public boolean isNotUniqueName(final ArtName name) {
        return artRepository.existsByNameValue(name.getValue());
    }

    public boolean isNameUsedByOther(final long artId, final ArtName name) {
        final Long nameUsedId = artRepository.findIdByName(name.getValue());
        return nameUsedId != null && !nameUsedId.equals(artId);
    }
}

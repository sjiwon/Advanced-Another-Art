package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtResourceValidator {
    private final ArtRepository artRepository;

    public void validatenNameIsUnique(final String name) {
        if (artRepository.existsByNameValue(name)) {
            throw AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME);
        }
    }

    public void validateNameIsInUseByOther(final Long artId, final String name) {
        if (artRepository.isNameUsedByOther(artId, name)) {
            throw AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME);
        }
    }
}

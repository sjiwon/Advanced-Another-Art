package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.DUPLICATE_NAME;

@Service
@RequiredArgsConstructor
public class ArtResourceValidator {
    private final ArtRepository artRepository;

    public void validatenNameIsUnique(final String name) {
        if (artRepository.existsByNameValue(name)) {
            throw new ArtException(DUPLICATE_NAME);
        }
    }

    public void validateNameIsInUseByOther(final Long artId, final String name) {
        if (artRepository.isNameUsedByOther(artId, name)) {
            throw new ArtException(DUPLICATE_NAME);
        }
    }
}

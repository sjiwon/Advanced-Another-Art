package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtValidator {
    private final ArtRepository artRepository;

    public void validateUniqueNameForCreate(final ArtName name) {
        if (artRepository.existsByName(name)) {
            throw AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME);
        }
    }

    public void validateUniqueNameForUpdate(final ArtName name, final Long artId) {
        if (artRepository.existsByNameAndIdNot(name, artId)) {
            throw AnotherArtException.type(ArtErrorCode.DUPLICATE_NAME);
        }
    }
}

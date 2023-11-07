package com.sjiwon.anotherart.art.application;

import com.sjiwon.anotherart.art.domain.model.ArtName;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
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

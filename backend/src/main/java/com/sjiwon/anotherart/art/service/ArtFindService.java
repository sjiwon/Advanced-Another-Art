package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtFindService {
    private final ArtRepository artRepository;

    public Art findById(final Long artId) {
        return artRepository.findById(artId)
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND));
    }

    public Art findByIdWithOwner(final Long artId) {
        return artRepository.findByIdWithOwner(artId)
                .orElseThrow(() -> AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND));
    }
}

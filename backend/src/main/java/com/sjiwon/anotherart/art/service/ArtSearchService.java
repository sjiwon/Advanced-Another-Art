package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtSearchService {
    private final ArtRepository artRepository;

    public ArtDetails getArt(Long artId) {
        ArtDetails response;
        if (isAuctionType(artId)) {
            response = artRepository.getAuctionArt(artId);
        } else {
            response = artRepository.getGeneralArt(artId);
        }

        validateResponseExists(response);
        return response;
    }

    private boolean isAuctionType(Long artId) {
        return artRepository.getArtTypeById(artId) == AUCTION;
    }

    private void validateResponseExists(ArtDetails response) {
        if (response == null) {
            throw AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND);
        }
    }
}

package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.ArtSingleQueryRepository;
import com.sjiwon.anotherart.art.domain.repository.query.response.ArtDetails;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ArtQueryUseCase {
    private final ArtSingleQueryRepository artSingleQueryRepository;

    public ArtDetails getArtById(final Long artId) {
        final Art.Type artType = artSingleQueryRepository.getArtType(artId);

        if (artType == Art.Type.AUCTION) {
            return artSingleQueryRepository.fetchAuctionArt(artId);
        }
        return artSingleQueryRepository.fetchGeneralArt(artId);
    }
}

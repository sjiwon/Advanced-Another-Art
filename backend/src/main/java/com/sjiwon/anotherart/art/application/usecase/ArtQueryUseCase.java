package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.query.response.ArtResponse;
import com.sjiwon.anotherart.art.application.usecase.query.response.AuctionArtResponse;
import com.sjiwon.anotherart.art.application.usecase.query.response.GeneralArtResponse;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.query.ArtBasicQueryRepository;
import com.sjiwon.anotherart.global.annotation.UseCase;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ArtQueryUseCase {
    private final ArtBasicQueryRepository artBasicQueryRepository;

    public ArtResponse getArtById(final Long artId) {
        final String artType = artBasicQueryRepository.getArtType(artId);

        if (isAuctionType(artType)) {
            return AuctionArtResponse.from(artBasicQueryRepository.fetchAuctionArt(artId));
        }
        return GeneralArtResponse.from(artBasicQueryRepository.fetchGeneralArt(artId));
    }

    private static boolean isAuctionType(final String artType) {
        return Art.Type.AUCTION.name().equals(artType);
    }
}

package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtDeletionPreInspector {
    private final AuctionRepository auctionRepository;

    public void checkArtCanBeDeleted(final Art art) {
        validateArtIsSold(art);
        validateArtBidderExists(art);
    }

    private void validateArtIsSold(final Art art) {
        if (art.isSold()) {
            throw AnotherArtException.type(ArtErrorCode.CANNOT_DELETE_SOLD_ART);
        }
    }

    private void validateArtBidderExists(final Art art) {
        if (art.isAuctionType() && isBidderExists(art.getId())) {
            throw AnotherArtException.type(ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS);
        }
    }

    private boolean isBidderExists(final Long artId) {
        return auctionRepository.isBidRecordExists(artId);
    }
}

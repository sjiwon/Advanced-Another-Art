package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.CANNOT_DELETE_IF_BID_EXISTS;
import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.CANNOT_DELETE_SOLD_ART;

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
            throw new ArtException(CANNOT_DELETE_SOLD_ART);
        }
    }

    private void validateArtBidderExists(final Art art) {
        if (art.isAuctionType() && isBidderExists(art.getId())) {
            throw new ArtException(CANNOT_DELETE_IF_BID_EXISTS);
        }
    }

    private boolean isBidderExists(final Long artId) {
        return auctionRepository.isBidRecordExists(artId);
    }
}

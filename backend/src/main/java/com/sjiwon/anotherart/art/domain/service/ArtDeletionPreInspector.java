package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.CANNOT_DELETE_IF_BID_EXISTS;
import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.CANNOT_DELETE_SOLD_ART;

@Service
@RequiredArgsConstructor
public class ArtDeletionPreInspector {
    private final AuctionReader auctionReader;

    public void checkArtCanBeDeleted(final Art art) {
        if (art.isSold()) {
            throw new ArtException(CANNOT_DELETE_SOLD_ART);
        }

        if (art.isAuctionType() && auctionReader.isBidRecordExists(art.getId())) {
            throw new ArtException(CANNOT_DELETE_IF_BID_EXISTS);
        }
    }
}

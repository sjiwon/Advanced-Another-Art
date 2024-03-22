package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.service.AuctionWriter;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.like.domain.service.LikeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtDeleter {
    private final AuctionWriter auctionWriter;
    private final LikeWriter likeWriter;
    private final ArtWriter artWriter;

    @AnotherArtWritableTransactional
    public void execute(final Art art) {
        if (art.isAuctionType()) {
            auctionWriter.deleteByArtId(art.getId());
        }

        likeWriter.deleteByArtId(art.getId());
        artWriter.deleteArt(art.getId());
    }
}

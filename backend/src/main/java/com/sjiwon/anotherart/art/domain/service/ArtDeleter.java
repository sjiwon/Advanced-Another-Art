package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.repository.HashtagRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtDeleter {
    private final AuctionRepository auctionRepository;
    private final FavoriteRepository favoriteRepository;
    private final HashtagRepository hashtagRepository;
    private final ArtRepository artRepository;

    @AnotherArtWritableTransactional
    public void execute(final Art art) {
        if (art.isAuctionType()) {
            auctionRepository.deleteByArtId(art.getId());
        }

        favoriteRepository.deleteByArtId(art.getId());
        hashtagRepository.deleteByArtId(art.getId());
        artRepository.deleteById(art.getId());
    }
}

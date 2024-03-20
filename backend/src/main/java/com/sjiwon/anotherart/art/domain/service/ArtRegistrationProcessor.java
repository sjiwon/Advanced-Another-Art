package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.Period;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArtRegistrationProcessor {
    private final ArtRepository artRepository;
    private final AuctionRepository auctionRepository;

    @AnotherArtWritableTransactional
    public Art execute(
            final Art art,
            final LocalDateTime auctionStartDate,
            final LocalDateTime auctionEndDate
    ) {
        final Art registrationArt = artRepository.save(art);
        registerAuctionViaType(registrationArt, auctionStartDate, auctionEndDate);
        return registrationArt;
    }

    private void registerAuctionViaType(
            final Art art,
            final LocalDateTime auctionStartDate,
            final LocalDateTime auctionEndDate
    ) {
        if (art.isAuctionType()) {
            final Auction auction = Auction.createAuction(art, Period.of(auctionStartDate, auctionEndDate));
            auctionRepository.save(auction);
        }
    }
}

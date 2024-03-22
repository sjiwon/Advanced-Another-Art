package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.Period;
import com.sjiwon.anotherart.auction.domain.service.AuctionWriter;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArtRegister {
    private final ArtWriter artWriter;
    private final AuctionWriter auctionWriter;

    @AnotherArtWritableTransactional
    public Art execute(
            final Art target,
            final LocalDateTime auctionStartDate,
            final LocalDateTime auctionEndDate
    ) {
        final Art art = artWriter.save(target);
        checkTypeAndApplyAuctionInstance(art, auctionStartDate, auctionEndDate);
        return art;
    }

    private void checkTypeAndApplyAuctionInstance(
            final Art art,
            final LocalDateTime auctionStartDate,
            final LocalDateTime auctionEndDate
    ) {
        if (art.isAuctionType()) {
            final Auction auction = Auction.createAuction(art, Period.of(auctionStartDate, auctionEndDate));
            auctionWriter.save(auction);
        }
    }
}

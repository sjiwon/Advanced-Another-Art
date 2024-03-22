package com.sjiwon.anotherart.auction.domain.service;

import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.auction.exception.AuctionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sjiwon.anotherart.auction.exception.AuctionExceptionCode.AUCTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuctionReader {
    private final AuctionRepository auctionRepository;

    public Auction getById(final Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));
    }

    public Auction getByIdWithRecords(final Long id) {
        return auctionRepository.findByIdWithRecords(id)
                .orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));
    }

    public Auction getByArtId(final Long artId) {
        return auctionRepository.findByArtId(artId)
                .orElseThrow(() -> new AuctionException(AUCTION_NOT_FOUND));
    }

    public boolean isBidRecordExists(final Long artId) {
        return auctionRepository.findHighestBidderIdByArtId(artId) != null;
    }
}

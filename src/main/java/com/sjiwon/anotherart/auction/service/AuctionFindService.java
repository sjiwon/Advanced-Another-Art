package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionFindService {
    private final AuctionRepository auctionRepository;

    public Auction findById(Long auctionId) {
        return auctionRepository.findById(auctionId)
                .orElseThrow(() -> AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_FOUND));
    }

    public Auction findByIdWithPessimisticLock(Long auctionId) {
        return auctionRepository.findByIdWithPessimisticLock(auctionId)
                .orElseThrow(() -> AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_FOUND));
    }

    public Auction findByArtId(Long artId) {
        return auctionRepository.findByArtId(artId)
                .orElseThrow(() -> AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_FOUND));
    }
}

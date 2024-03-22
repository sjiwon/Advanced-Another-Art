package com.sjiwon.anotherart.auction.domain.service;

import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRecordRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionWriter {
    private final AuctionRepository auctionRepository;
    private final AuctionRecordRepository auctionRecordRepository;

    public Auction save(final Auction target) {
        return auctionRepository.save(target);
    }

    public AuctionRecord saveRecord(final Auction auction, final Member bidder, final int bidPrice) {
        return auctionRecordRepository.save(new AuctionRecord(auction, bidder, bidPrice));
    }

    @AnotherArtWritableTransactional
    public void deleteByArtId(final long artId) {
        final Long auctionId = auctionRepository.findIdByArtId(artId);
        auctionRecordRepository.deleteAuctionRecords(auctionId);
        auctionRepository.deleteAuction(auctionId);
    }
}

package com.sjiwon.anotherart.auction.domain.service;

import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidProcessor {
    private final MemberReader memberReader;
    private final AuctionWriter auctionWriter;

    @AnotherArtWritableTransactional
    public void execute(
            final Auction auction,
            final Member newBidder,
            final int newBidPrice
    ) {
        final Long previousBidderId = auction.getHighestBidderId();
        final int previousBidPrice = auction.getHighestBidPrice();

        // 1. 최고 입찰자 정보 갱신
        auction.updateHighestBid(newBidder, newBidPrice);
        auctionWriter.saveRecord(auction, newBidder, newBidPrice);

        // 2. 이전 입찰자 <-> 새로운 입찰자 포인트 트랜잭션
        doPointTransaction(previousBidderId, newBidder, previousBidPrice, newBidPrice);
    }

    private void doPointTransaction(
            final Long previousBidderId,
            final Member newBidder,
            final int previousBidPrice,
            final int newBidPrice
    ) {
        newBidder.decreaseAvailablePoint(newBidPrice);

        if (previousBidderId != null) {
            final Member previousBidder = memberReader.getById(previousBidderId);
            previousBidder.increaseAvailablePoint(previousBidPrice);
        }
    }
}

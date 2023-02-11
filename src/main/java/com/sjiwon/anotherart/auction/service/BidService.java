package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final AuctionFindService auctionFindService;
    private final MemberFindService memberFindService;

    @Transactional
    public void bid(Long auctionId, Long bidderId, int bidAmount) {
        Auction auction = auctionFindService.findByIdWithPessimisticLock(auctionId);
        Member newBidder = memberFindService.findById(bidderId);
        executeBidProcess(auction, newBidder, bidAmount);
    }

    private void executeBidProcess(Auction auction, Member newBidder, int bidAmount) {
        auction.applyNewBid(newBidder, bidAmount);
        auction.addAuctionRecord(AuctionRecord.createAuctionRecord(auction, newBidder, bidAmount));
    }
}

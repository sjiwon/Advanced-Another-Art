package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.member.application.MemberFindService;
import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidService {
    private final MemberFindService memberFindService;
    private final AuctionFindService auctionFindService;

    @Transactional
    public void bid(final Long auctionId, final Long bidderId, final Integer bidPrice) {
        final Auction auction = auctionFindService.findById(auctionId);
        final Member bidder = memberFindService.findById(bidderId);

        auction.applyNewBid(bidder, bidPrice);
    }
}

package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BidService {
    private final MemberRepository memberRepository;
    private final AuctionFindService auctionFindService;

    @Transactional
    public void bid(final Long auctionId, final Long bidderId, final Integer bidPrice) {
        final Auction auction = auctionFindService.findById(auctionId);
        final Member bidder = memberRepository.getById(bidderId);

        auction.applyNewBid(bidder, bidPrice);
    }
}

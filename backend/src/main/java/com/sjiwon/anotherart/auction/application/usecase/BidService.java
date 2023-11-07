package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
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
    private final AuctionRepository auctionRepository;

    @Transactional
    public void bid(final Long auctionId, final Long bidderId, final Integer bidPrice) {
        final Auction auction = auctionRepository.getById(auctionId);
        final Member bidder = memberRepository.getById(bidderId);

        auction.applyNewBid(bidder, bidPrice);
    }
}

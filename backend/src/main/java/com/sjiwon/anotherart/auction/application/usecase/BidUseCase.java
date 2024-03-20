package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.lock.DistributedLock;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class BidUseCase {
    private final AuctionRepository auctionRepository;
    private final MemberRepository memberRepository;

    @DistributedLock(
            keyPrefix = "AUCTION:",
            keySuffix = "#command.auctionId",
            withInTransaction = true,
            withRetry = 3
    )
    public void invoke(final BidCommand command) {
        final Auction auction = auctionRepository.getByIdWithFetchBidder(command.auctionId());
        final Member bidder = memberRepository.getById(command.memberId());

        auction.applyNewBid(bidder, command.bidPrice());
    }
}

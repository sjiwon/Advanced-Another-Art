package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import com.sjiwon.anotherart.auction.domain.service.BidInspector;
import com.sjiwon.anotherart.auction.domain.service.BidProcessor;
import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.lock.DistributedLock;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class BidUseCase {
    private final AuctionReader auctionReader;
    private final ArtReader artReader;
    private final MemberReader memberReader;
    private final BidInspector bidInspector;
    private final BidProcessor bidProcessor;

    @DistributedLock(
            keyPrefix = "AUCTION:",
            keySuffix = "#command.auctionId",
            withInTransaction = true,
            withRetry = 3
    )
    public void invoke(final BidCommand command) {
        final Auction auction = auctionReader.getById(command.auctionId());
        final Art art = artReader.getById(auction.getArtId());
        final Member bidder = memberReader.getById(command.memberId());

        bidInspector.checkBidCanBeProceed(auction, art, bidder, command.bidPrice());
        bidProcessor.execute(auction, bidder, command.bidPrice());
    }
}

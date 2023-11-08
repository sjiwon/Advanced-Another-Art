package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.purchase.application.usecase.command.PurchaseArtCommand;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseArtUseCase {
    private final ArtRepository artRepository;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;
    private final PurchaseRepository purchaseRepository;

    // TODO need `Concurrency Control`
    @AnotherArtWritableTransactional
    public void invoke(final PurchaseArtCommand command) {
        final Art art = artRepository.getByIdWithFetchOwner(command.artId());
        final Member buyer = memberRepository.getById(command.memberId());

        proceedPurchase(art, buyer);
    }

    private void proceedPurchase(final Art art, final Member buyer) {
        if (art.isAuctionType()) {
            final Auction auction = auctionRepository.getByArtId(art.getId());
            validateAuctionArtIsPurchasable(auction, buyer);
            purchaseRepository.save(Purchase.purchaseAuctionArt(art, buyer, auction.getHighestBidPrice()));
        } else {
            purchaseRepository.save(Purchase.purchaseGeneralArt(art, buyer));
        }
    }

    private void validateAuctionArtIsPurchasable(final Auction auction, final Member buyer) {
        if (auction.isInProgress()) {
            throw AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED);
        }

        if (!auction.isHighestBidder(buyer)) {
            throw AnotherArtException.type(PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER);
        }
    }
}

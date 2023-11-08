package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.model.PointType;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import com.sjiwon.anotherart.purchase.application.usecase.command.PurchaseArtCommand;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseArtUseCase {
    private final ArtRepository artRepository;
    private final MemberRepository memberRepository;
    private final AuctionRepository auctionRepository;
    private final PointRecordRepository pointRecordRepository;
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

            final Purchase purchaseAuctionArt = Purchase.purchaseAuctionArt(art, buyer, auction.getHighestBidPrice());
            purchaseRepository.save(purchaseAuctionArt);

            applyPointRecordOfOwnerAndBuyer(art.getOwner(), buyer, auction.getHighestBidPrice());
        } else {
            final Purchase purchaseGeneralArt = Purchase.purchaseGeneralArt(art, buyer);
            purchaseRepository.save(purchaseGeneralArt);

            applyPointRecordOfOwnerAndBuyer(art.getOwner(), buyer, art.getPrice());
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

    private void applyPointRecordOfOwnerAndBuyer(final Member owner, final Member buyer, final int amount) {
        pointRecordRepository.saveAll(List.of(
                PointRecord.addPointRecord(owner, PointType.SOLD, amount),
                PointRecord.addPointRecord(buyer, PointType.PURCHASE, amount)
        ));
    }
}

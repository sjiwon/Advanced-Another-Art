package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.lock.DistributedLock;
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
import org.springframework.dao.DataIntegrityViolationException;
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

    @DistributedLock(
            keyPrefix = "ART:",
            keySuffix = "#command.artId",
            withInTransaction = true
    )
    public void invoke(final PurchaseArtCommand command) {
        final Art art = artRepository.getByIdWithFetchOwner(command.artId());
        final Member buyer = memberRepository.getById(command.memberId());

        proceedPurchase(art, buyer);
    }

    private void proceedPurchase(final Art art, final Member buyer) {
        if (art.isAuctionType()) {
            final Auction auction = auctionRepository.getByArtId(art.getId());
            validateAuctionArtIsPurchasable(auction, buyer);

            final Purchase purchase = Purchase.purchaseAuctionArt(art, buyer, auction.getHighestBidPrice());
            doPurchase(purchase, art.getOwner(), buyer);
        } else {
            final Purchase purchase = Purchase.purchaseGeneralArt(art, buyer);
            doPurchase(purchase, art.getOwner(), buyer);
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

    private void doPurchase(final Purchase purchase, final Member owner, final Member buyer) {
        try {
            purchaseRepository.save(purchase);
            pointRecordRepository.saveAll(List.of(
                    PointRecord.addPointRecord(owner, PointType.SOLD, purchase.getPrice()),
                    PointRecord.addPointRecord(buyer, PointType.PURCHASE, purchase.getPrice())
            ));
        } catch (final DataIntegrityViolationException e) {
            // Redis Timeout 발생 시 일반 작품 구매 동시 Insert에 대한 Unique Constraint
            throw AnotherArtException.type(PurchaseErrorCode.ALREADY_SOLD);
        }
    }
}

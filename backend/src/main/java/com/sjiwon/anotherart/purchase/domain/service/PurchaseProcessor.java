package com.sjiwon.anotherart.purchase.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.service.PointRecordWriter;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseProcessor {
    private final AuctionReader auctionReader;
    private final PurchaseInspector purchaseInspector;
    private final PurchaseWriter purchaseWriter;
    private final PointRecordWriter pointRecordWriter;
    private final AssociatedPointTransactionProcessor associatedPointTransactionProcessor;

    @AnotherArtWritableTransactional
    public void purchaseAuctionArt(
            final Art art,
            final Member owner,
            final Member buyer
    ) {
        final Auction auction = auctionReader.getByArtId(art.getId());
        purchaseInspector.checkAuctionArt(auction, art, buyer);
        art.closeSale();

        final Purchase purchase = Purchase.purchaseAuctionArt(art, buyer, auction.getHighestBidPrice());
        proceedPurchase(purchase, owner, buyer);
        associatedPointTransactionProcessor.executeWithPurchaseAuction(owner, buyer, purchase.getPrice());
    }

    @AnotherArtWritableTransactional
    public void purchaseGeneralArt(
            final Art art,
            final Member owner,
            final Member buyer
    ) {
        purchaseInspector.checkGeneralArt(art, buyer);
        art.closeSale();

        final Purchase purchase = Purchase.purchaseGeneralArt(art, buyer);
        proceedPurchase(purchase, owner, buyer);
        associatedPointTransactionProcessor.executeWithPurchaseGeneral(owner, buyer, purchase.getPrice());
    }

    private void proceedPurchase(
            final Purchase purchase,
            final Member owner,
            final Member buyer
    ) {
        purchaseWriter.save(purchase);
        pointRecordWriter.save(
                PointRecord.addArtSoldRecord(owner, purchase.getPrice()),
                PointRecord.addArtPurchaseRecord(buyer, purchase.getPrice())
        );
    }
}

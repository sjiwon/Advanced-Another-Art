package com.sjiwon.anotherart.purchase.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art_purchase")
public class Purchase extends BaseEntity<Purchase> {
    @Column(name = "purchase_price", nullable = false, updatable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Member buyer;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    private Art art;

    private Purchase(final Art art, final Member buyer, final int price) {
        this.art = art;
        this.buyer = buyer;
        this.price = price;
    }

    public static Purchase purchaseGeneralArt(final Art art, final Member buyer) {
        validateBuyerIsArtOwner(art, buyer);
        validateArtIsOnSale(art);
        return proceedToPurchaseGeneralArt(art, buyer);
    }

    public static Purchase purchaseAuctionArt(final Art art, final Member buyer, final int bidPrice) {
        validateArtIsOnSale(art);
        return proceedToPurchaseAuctionArt(art, buyer, bidPrice);
    }

    private static void validateBuyerIsArtOwner(final Art art, final Member buyer) {
        if (art.isOwner(buyer)) {
            throw AnotherArtException.type(PurchaseErrorCode.ART_OWNER_CANNOT_PURCHASE_OWN);
        }
    }

    private static void validateArtIsOnSale(final Art art) {
        if (art.isSold()) {
            throw AnotherArtException.type(PurchaseErrorCode.ALREADY_SOLD);
        }
    }

    private static Purchase proceedToPurchaseGeneralArt(final Art art, final Member buyer) {
        final Member owner = art.getOwner();
        final int price = art.getPrice();

        // 1. 구매자 포인트 차감
        buyer.decreaseTotalPoint(price);

        // 2. 판매자 포인트 적립
        owner.increaseTotalPoint(price);

        // 3. 구매
        art.closeSale();
        return new Purchase(art, buyer, price);
    }

    private static Purchase proceedToPurchaseAuctionArt(final Art art, final Member buyer, final int bidPrice) {
        final Member owner = art.getOwner();

        // 1. 구매자 포인트 차감
        buyer.decreaseTotalPoint(bidPrice);
        buyer.increaseAvailablePoint(bidPrice); // 입찰 시 소모한 포인트 누적 차감 문제 해결

        // 2. 판매자 포인트 적립
        owner.increaseTotalPoint(bidPrice);

        // 3. 구매
        art.closeSale();
        return new Purchase(art, buyer, bidPrice);
    }
}

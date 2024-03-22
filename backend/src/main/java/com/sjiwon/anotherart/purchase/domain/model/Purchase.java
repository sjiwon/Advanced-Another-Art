package com.sjiwon.anotherart.purchase.domain.model;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "art_purchase")
public class Purchase extends BaseEntity<Purchase> {
    @Column(name = "art_id", nullable = false, updatable = false, unique = true)
    private Long artId;

    @Column(name = "buyer_id", nullable = false, updatable = false)
    private Long buyerId;

    @Column(name = "purchase_price", nullable = false, updatable = false)
    private int price;

    private Purchase(final Art art, final Member buyer, final int price) {
        this.artId = art.getId();
        this.buyerId = buyer.getId();
        this.price = price;
    }

    public static Purchase purchaseGeneralArt(final Art art, final Member buyer) {
        return new Purchase(art, buyer, art.getPrice());
    }

    public static Purchase purchaseAuctionArt(final Art art, final Member buyer, final int bidPrice) {
        return new Purchase(art, buyer, bidPrice);
    }
}

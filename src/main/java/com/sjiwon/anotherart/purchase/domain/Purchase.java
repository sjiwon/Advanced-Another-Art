package com.sjiwon.anotherart.purchase.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "purchase")
@EntityListeners(AuditingEntityListener.class)
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_price", nullable = false, updatable = false)
    private int purchasePrice;

    @CreatedDate
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false, unique = true)
    private Art art;

    @Builder
    private Purchase(Member member, Art art, int purchasePrice) {
        this.member = member;
        this.art = art;
        this.purchasePrice = purchasePrice;
    }

    public static Purchase purchaseArt(Member member, Art art, int purchasePrice) {
        validatePurchaseAvailability(art);
        validateMemberAvailablePoint(member, purchasePrice);
        changeSaleStatus(art);
        return new Purchase(member, art, purchasePrice);
    }

    private static void validatePurchaseAvailability(Art art) {
        if (art.isSoldOut()) {
            throw AnotherArtException.type(PurchaseErrorCode.ART_ALREADY_SOLD_OUT);
        }
    }

    private static void validateMemberAvailablePoint(Member member, int purchasePrice) {
        if (member.isPointInsufficient(purchasePrice)) {
            throw AnotherArtException.type(PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT);
        }
    }

    private static void changeSaleStatus(Art art) {
        art.changeArtStatus(ArtStatus.SOLD_OUT);
    }
}

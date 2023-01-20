package com.sjiwon.anotherart.purchase.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.member.domain.Member;
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

    public static Purchase applyPurchase(Member member, Art art, int purchasePrice) {
        return new Purchase(member, art, purchasePrice);
    }
}

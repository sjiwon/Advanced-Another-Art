package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

import static com.sjiwon.anotherart.art.domain.model.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.model.ArtStatus.SOLD;
import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "art")
public class Art extends BaseEntity<Art> {
    @Embedded
    private ArtName name;

    @Embedded
    private Description description;

    @Enumerated(STRING)
    @Column(name = "art_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(30)")
    private ArtType type;

    @Column(name = "price", nullable = false, updatable = false)
    private int price;

    @Embedded
    private UploadImage uploadImage;

    @Enumerated(STRING)
    @Column(name = "art_status", nullable = false, columnDefinition = "VARCHAR(30)")
    private ArtStatus status;

    @Embedded
    private Hashtags hashtags;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Member owner;

    private Art(
            final Member owner,
            final ArtName name,
            final Description description,
            final ArtType type,
            final int price,
            final UploadImage uploadImage,
            final Set<String> hashtags
    ) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.uploadImage = uploadImage;
        this.status = ON_SALE;
        this.hashtags = new Hashtags(this, hashtags);
    }

    public static Art createArt(
            final Member owner,
            final ArtName name,
            final Description description,
            final ArtType type,
            final int price,
            final UploadImage uploadImage,
            final Set<String> hashtags
    ) {
        return new Art(owner, name, description, type, price, uploadImage, hashtags);
    }

    public void update(final ArtName name, final Description description, final Set<String> hashtags) {
        this.name = name;
        this.description = description;
        this.hashtags.update(this, hashtags);
    }

    public void closeSale() {
        this.status = SOLD;
    }

    public boolean isSold() {
        return this.status == SOLD;
    }

    public boolean isAuctionType() {
        return this.type == AUCTION;
    }

    public boolean isOwner(final Member other) {
        return owner.isSame(other);
    }

    // Add Getter
    public List<String> getHashtags() {
        return hashtags.getHashtags()
                .stream()
                .map(Hashtag::getName)
                .toList();
    }
}

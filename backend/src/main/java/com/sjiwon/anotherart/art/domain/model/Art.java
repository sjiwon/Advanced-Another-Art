package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.global.base.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.sjiwon.anotherart.art.domain.model.Art.Status.ON_SALE;
import static com.sjiwon.anotherart.art.domain.model.Art.Status.SOLD;
import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.INVALID_ART_TYPE;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "art")
public class Art extends BaseEntity<Art> {
    @Column(name = "owner_id", nullable = false, updatable = false)
    private Long ownerId;

    @Embedded
    private ArtName name;

    @Embedded
    private Description description;

    @Enumerated(STRING)
    @Column(name = "art_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(30)")
    private Type type;

    @Column(name = "price", nullable = false, updatable = false)
    private int price;

    @Embedded
    private UploadImage uploadImage;

    @Enumerated(STRING)
    @Column(name = "art_status", nullable = false, columnDefinition = "VARCHAR(30)")
    private Status status;

    @Embedded
    private Hashtags hashtags;

    private Art(
            final Member owner,
            final ArtName name,
            final Description description,
            final Type type,
            final int price,
            final UploadImage uploadImage,
            final Set<String> hashtags
    ) {
        this.ownerId = owner.getId();
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
            final Type type,
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
        return this.type == Type.AUCTION;
    }

    public boolean isOwner(final Member compare) {
        return ownerId.equals(compare.getId());
    }

    // Add Getter
    public List<String> getHashtags() {
        return hashtags.getHashtags()
                .stream()
                .map(Hashtag::getName)
                .toList();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        GENERAL("일반 작품", "general"),
        AUCTION("경매 작품", "auction"),
        ;

        private final String description;
        private final String label;

        public static Type from(final String label) {
            return Arrays.stream(values())
                    .filter(it -> it.label.equals(label))
                    .findFirst()
                    .orElseThrow(() -> new ArtException(INVALID_ART_TYPE));
        }
    }

    public enum Status {
        ON_SALE, SOLD
    }
}

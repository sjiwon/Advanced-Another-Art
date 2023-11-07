package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.member.domain.model.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sjiwon.anotherart.art.domain.model.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.model.ArtStatus.SOLD;
import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art")
public class Art extends BaseEntity<Art> {
    @Embedded
    private ArtName name;

    @Embedded
    private Description description;

    @Enumerated(EnumType.STRING)
    @Column(name = "art_type", nullable = false, updatable = false)
    private ArtType type;

    @Column(name = "price", nullable = false, updatable = false)
    private int price;

    @Column(name = "storage_name", nullable = false)
    private String storageName;

    @Enumerated(EnumType.STRING)
    @Column(name = "art_status", nullable = false)
    private ArtStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Member owner;

    @OneToMany(mappedBy = "art", cascade = CascadeType.PERSIST)
    private final List<Hashtag> hashtags = new ArrayList<>();

    private Art(final Member owner, final ArtName name, final Description description, final ArtType type,
                final int price, final String storageName, final Set<String> hashtags) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.storageName = storageName;
        this.status = ON_SALE;
        applyHashtags(hashtags);
    }

    public static Art createArt(final Member owner, final ArtName name, final Description description, final ArtType type,
                                final int price, final String storageName, final Set<String> hashtags) {
        return new Art(owner, name, description, type, price, storageName, hashtags);
    }

    public void update(final ArtName name, final Description description, final Set<String> hashtags) {
        this.name = name;
        this.description = description;
        applyHashtags(hashtags);
    }

    public void applyHashtags(final Set<String> hashtags) {
        this.hashtags.clear();
        this.hashtags.addAll(
                hashtags.stream()
                        .map(value -> Hashtag.applyHashtag(this, value))
                        .toList()
        );
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

    public boolean isArtOwner(final Member other) {
        return owner.isSameMember(other);
    }

    // Add Getter
    public String getNameValue() {
        return name.getValue();
    }

    public String getDescriptionValue() {
        return description.getValue();
    }

    public List<String> getHashtags() {
        return hashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());
    }
}

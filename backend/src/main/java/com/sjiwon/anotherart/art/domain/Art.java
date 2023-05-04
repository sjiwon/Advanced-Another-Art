package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.domain.hashtag.Hashtag;
import com.sjiwon.anotherart.global.BaseEntity;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.ArtStatus.SOLD;
import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art")
public class Art extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "art", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>();

    private Art(Member owner, ArtName name, Description description, ArtType type,
                int price, String storageName, Set<String> hashtags) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
        this.storageName = storageName;
        this.status = ON_SALE;
        applyHashtags(hashtags);
    }

    public static Art createArt(Member owner, ArtName name, Description description, ArtType type,
                                int price, String storageName, Set<String> hashtags) {
        return new Art(owner, name, description, type, price, storageName, hashtags);
    }

    public void update(ArtName name, Description description, Set<String> hashtags) {
        this.name = name;
        this.description = description;
        applyHashtags(hashtags);
    }

    public void applyHashtags(Set<String> hashtags) {
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

    public boolean isArtOwner(Member other) {
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

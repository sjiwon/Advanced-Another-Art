package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.domain.hashtag.Hashtag;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art")
@EntityListeners(AuditingEntityListener.class)
public class Art {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, updatable = false, unique = true, length = 100)
    private String name;

    @Lob
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "art_type", nullable = false, updatable = false, length = 10)
    private ArtType artType;

    @Column(name = "price", nullable = false, updatable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(name = "art_status", nullable = false, length = 10)
    private ArtStatus artStatus;

    @Embedded
    private UploadImage uploadImage;

    @CreatedDate
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Member member;

    @OneToMany(mappedBy = "art", cascade = {CascadeType.PERSIST}, orphanRemoval = true)
    private Set<Hashtag> hashtags = new HashSet<>();

    @Builder
    private Art(String name, String description, ArtType artType, int price, UploadImage uploadImage, Member member) {
        this.name = name;
        this.description = description;
        this.artType = artType;
        this.price = price;
        this.artStatus = ArtStatus.FOR_SALE;
        this.uploadImage = uploadImage;
        this.member = member;
    }

    public static Art createArt(String name, String description, ArtType artType, int price, UploadImage uploadImage, Member member) {
        return new Art(name, description, artType, price, uploadImage, member);
    }

    public void insertHashtags(Set<String> hashtags) {
        this.getHashtags().addAll(
                hashtags.stream()
                        .map(value -> Hashtag.from(this, value))
                        .collect(Collectors.toSet())
        );
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateArtStatus(ArtStatus artStatus) {
        this.artStatus = artStatus;
    }

    public boolean isArtOwner(Long memberId) {
        return Objects.equals(this.member.getId(), memberId);
    }

    public boolean isSoldOut() {
        return this.artStatus == ArtStatus.SOLD_OUT;
    }
}

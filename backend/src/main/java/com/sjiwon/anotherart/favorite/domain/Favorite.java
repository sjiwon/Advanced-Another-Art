package com.sjiwon.anotherart.favorite.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art_favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "art_id", nullable = false, updatable = false)
    private Long artId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    private Favorite(final Long artId, final Long memberId) {
        this.artId = artId;
        this.memberId = memberId;
    }

    public static Favorite favoriteMarking(final Long artId, final Long memberId) {
        return new Favorite(artId, memberId);
    }
}

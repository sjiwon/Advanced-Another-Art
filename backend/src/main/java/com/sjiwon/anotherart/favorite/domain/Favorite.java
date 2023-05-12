package com.sjiwon.anotherart.favorite.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    private Favorite(Long artId, Long memberId) {
        this.artId = artId;
        this.memberId = memberId;
    }

    public static Favorite favoriteMarking(Long artId, Long memberId) {
        return new Favorite(artId, memberId);
    }
}

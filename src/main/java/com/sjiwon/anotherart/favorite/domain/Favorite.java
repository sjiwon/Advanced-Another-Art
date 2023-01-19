package com.sjiwon.anotherart.favorite.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "favorite")
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "art_id", nullable = false)
    private Long artId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    private Favorite(Long artId, Long userId) {
        this.artId = artId;
        this.userId = userId;
    }

    public static Favorite favoriteMarking(Long artId, Long userId) {
        return new Favorite(artId, userId);
    }
}

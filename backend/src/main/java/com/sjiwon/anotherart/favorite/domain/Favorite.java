package com.sjiwon.anotherart.favorite.domain;

import com.sjiwon.anotherart.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art_favorite")
public class Favorite extends BaseEntity<Favorite> {
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

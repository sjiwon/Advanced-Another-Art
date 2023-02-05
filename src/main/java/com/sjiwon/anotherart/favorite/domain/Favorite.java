package com.sjiwon.anotherart.favorite.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.member.domain.Member;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Art art;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Member member;

    @Builder
    private Favorite(Art art, Member member) {
        this.art = art;
        this.member = member;
    }

    public static Favorite favoriteMarking(Art art, Member member) {
        return new Favorite(art, member);
    }
}

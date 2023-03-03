package com.sjiwon.anotherart.art.domain.hashtag;

import com.sjiwon.anotherart.art.domain.Art;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "art_hashtag")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false)
    private Art art;

    @Builder
    private Hashtag(Art art, String name) {
        this.art = art;
        this.name = name;
    }

    public static Hashtag from(Art art, String name) {
        return new Hashtag(art, name);
    }
}

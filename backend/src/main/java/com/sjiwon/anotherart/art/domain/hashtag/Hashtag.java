package com.sjiwon.anotherart.art.domain.hashtag;

import com.sjiwon.anotherart.art.domain.Art;
import lombok.AccessLevel;
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

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "art_id", referencedColumnName = "id", nullable = false)
    private Art art;

    private Hashtag(Art art, String name) {
        this.art = art;
        this.name = name;
    }

    public static Hashtag applyHashtag(Art art, String name) {
        return new Hashtag(art, name);
    }
}

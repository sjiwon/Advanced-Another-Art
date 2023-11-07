package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Hashtags {
    private static final int MIN_COUNT = 1;
    private static final int MAX_COUNT = 10;

    @OneToMany(mappedBy = "art", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<Hashtag> hashtags = new ArrayList<>();

    public Hashtags(final Art art, final Set<String> hashtags) {
        validateHashtagCount(hashtags);
        applyHashtags(art, hashtags);
    }

    public void update(final Art art, final Set<String> hashtags) {
        validateHashtagCount(hashtags);

        this.hashtags.clear();
        applyHashtags(art, hashtags);
    }

    private void validateHashtagCount(final Set<String> hashtags) {
        if (hashtags.isEmpty()) {
            throw AnotherArtException.type(ArtErrorCode.HASHTAG_MUST_EXISTS_AT_LEAST_ONE);
        }

        if (hashtags.size() > MAX_COUNT) {
            throw AnotherArtException.type(ArtErrorCode.HASHTAG_MUST_NOT_EXISTS_MORE_THAN_TEN);
        }
    }

    private void applyHashtags(final Art art, final Set<String> hashtags) {
        this.hashtags.addAll(
                hashtags.stream()
                        .map(value -> Hashtag.applyHashtag(art, value))
                        .toList()
        );
    }
}

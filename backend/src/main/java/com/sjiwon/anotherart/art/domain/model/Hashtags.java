package com.sjiwon.anotherart.art.domain.model;

import com.sjiwon.anotherart.art.exception.ArtException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.HASHTAG_MUST_EXISTS_AT_LEAST_ONE;
import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.HASHTAG_MUST_NOT_EXISTS_MORE_THAN_TEN;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
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
            throw new ArtException(HASHTAG_MUST_EXISTS_AT_LEAST_ONE);
        }

        if (hashtags.size() > MAX_COUNT) {
            throw new ArtException(HASHTAG_MUST_NOT_EXISTS_MORE_THAN_TEN);
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

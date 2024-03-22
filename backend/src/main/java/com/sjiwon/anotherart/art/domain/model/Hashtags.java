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

import static com.sjiwon.anotherart.art.exception.ArtExceptionCode.HASHTAG_MUST_BE_EXISTS_WITHIN_RESTRICTIONS;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Hashtags {
    private static final int MAX_COUNT = 10;

    @OneToMany(mappedBy = "art", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private final List<Hashtag> hashtags = new ArrayList<>();

    public Hashtags(final Art art, final Set<String> hashtags) {
        applyHashtags(art, hashtags);
    }

    public void update(final Art art, final Set<String> hashtags) {
        applyHashtags(art, hashtags);
    }

    private void applyHashtags(final Art art, final Set<String> hashtags) {
        validateHashtagCount(hashtags);

        this.hashtags.clear();
        this.hashtags.addAll(
                hashtags.stream()
                        .map(it -> new Hashtag(art, it))
                        .toList()
        );
    }

    private void validateHashtagCount(final Set<String> hashtags) {
        if (hashtags.isEmpty() || hashtags.size() > MAX_COUNT) {
            throw new ArtException(HASHTAG_MUST_BE_EXISTS_WITHIN_RESTRICTIONS);
        }
    }
}

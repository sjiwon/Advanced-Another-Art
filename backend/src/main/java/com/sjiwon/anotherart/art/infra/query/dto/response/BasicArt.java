package com.sjiwon.anotherart.art.infra.query.dto.response;

import com.sjiwon.anotherart.art.domain.ArtName;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.art.domain.Description;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BasicArt {
    private final Long id;
    private final String name;
    private final String description;
    private final int price;
    private final String status;
    private final String storageName;
    private final LocalDateTime registrationDate;
    private List<String> hashtags;
    private List<Long> likeMembers;

    public BasicArt(final Long id, final ArtName name, final Description description, final int price, final ArtStatus status,
                    final String storageName, final LocalDateTime registrationDate) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.price = price;
        this.status = status.getDescription();
        this.storageName = storageName;
        this.registrationDate = registrationDate;
    }

    public void applyHashtags(final List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyLikeMembers(final List<Long> likeMembers) {
        this.likeMembers = likeMembers;
    }
}

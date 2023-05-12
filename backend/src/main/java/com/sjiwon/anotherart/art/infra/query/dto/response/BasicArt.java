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

    public BasicArt(Long id, ArtName name, Description description, int price, ArtStatus status,
                    String storageName, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name.getValue();
        this.description = description.getValue();
        this.price = price;
        this.status = status.getDescription();
        this.storageName = storageName;
        this.registrationDate = registrationDate;
    }

    public void applyHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void applyLikeMembers(List<Long> likeMembers) {
        this.likeMembers = likeMembers;
    }
}

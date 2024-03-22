package com.sjiwon.anotherart.art.application.usecase.query.response;

import java.time.LocalDateTime;
import java.util.List;

public record ArtSummary(
        Long id,
        String name,
        String description,
        int price,
        String status,
        String storageUrl,
        LocalDateTime registrationDate,
        List<String> hashtags,
        List<Long> likeMembers
) {
}

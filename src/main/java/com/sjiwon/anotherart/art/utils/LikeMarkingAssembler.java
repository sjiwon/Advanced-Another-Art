package com.sjiwon.anotherart.art.utils;

import com.sjiwon.anotherart.art.infra.query.dto.FavoriteSummary;

import java.util.List;
import java.util.stream.Collectors;

public class LikeMarkingAssembler {
    public static List<Long> extractLikeMarkingMemberListByArtId(List<FavoriteSummary> favoriteSummaries, Long artId) {
        return favoriteSummaries.stream()
                .filter(simpleLikeArt -> simpleLikeArt.getArtId().equals(artId))
                .map(FavoriteSummary::getMemberId)
                .collect(Collectors.toList());
    }
}

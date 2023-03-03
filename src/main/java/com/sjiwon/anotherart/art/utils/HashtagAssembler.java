package com.sjiwon.anotherart.art.utils;

import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;

import java.util.List;
import java.util.stream.Collectors;

public class HashtagAssembler {
    public static List<String> extractHashtagListByArtId(List<HashtagSummary> hashtagSummaries, Long artId) {
        return hashtagSummaries.stream()
                .filter(simpleHashtag -> simpleHashtag.getArtId().equals(artId))
                .map(HashtagSummary::getName)
                .collect(Collectors.toList());
    }
}

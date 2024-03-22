package com.sjiwon.anotherart.art.domain.repository.query.spec;

public record ArtDetailsSearchCondition(
        SortType sortType,
        String value
) {
}

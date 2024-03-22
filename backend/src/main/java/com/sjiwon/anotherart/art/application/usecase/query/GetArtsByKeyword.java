package com.sjiwon.anotherart.art.application.usecase.query;

import com.sjiwon.anotherart.art.domain.repository.query.spec.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.SortType;
import com.sjiwon.anotherart.global.query.PageCreator;
import org.springframework.data.domain.Pageable;

public record GetArtsByKeyword(
        SortType sortType,
        String keyword,
        int page
) {
    public ArtDetailsSearchCondition createCondition() {
        return new ArtDetailsSearchCondition(sortType, keyword);
    }

    public Pageable createPage() {
        return PageCreator.create(page);
    }
}

package com.sjiwon.anotherart.art.application.usecase.query;

import com.sjiwon.anotherart.art.domain.repository.query.spec.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.domain.repository.query.spec.SortType;
import com.sjiwon.anotherart.global.query.PageCreator;
import org.springframework.data.domain.Pageable;

public record GetActiveAuctionArts(
        SortType sortType,
        int page
) {
    public ActiveAuctionArtsSearchCondition createCondition() {
        return new ActiveAuctionArtsSearchCondition(sortType);
    }

    public Pageable createPage() {
        return PageCreator.create(page);
    }
}

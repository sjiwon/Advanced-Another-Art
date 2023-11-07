package com.sjiwon.anotherart.art.application.usecase.query;

import com.sjiwon.anotherart.art.utils.search.ActiveAuctionArtsSearchCondition;
import org.springframework.data.domain.Pageable;

public record GetActiveAuctionArts(
        ActiveAuctionArtsSearchCondition condition,
        Pageable pageable
) {
}

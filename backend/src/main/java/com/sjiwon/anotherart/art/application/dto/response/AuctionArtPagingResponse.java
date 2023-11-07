package com.sjiwon.anotherart.art.application.dto.response;

import com.sjiwon.anotherart.art.domain.repository.query.dto.AuctionArt;
import com.sjiwon.anotherart.art.utils.search.Pagination;

import java.util.List;

public record AuctionArtPagingResponse(
        List<AuctionArt> result,
        Pagination pagination
) {
}
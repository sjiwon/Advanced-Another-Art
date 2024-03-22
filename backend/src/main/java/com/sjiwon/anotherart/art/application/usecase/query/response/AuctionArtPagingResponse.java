package com.sjiwon.anotherart.art.application.usecase.query.response;

import com.sjiwon.anotherart.art.domain.repository.query.response.AuctionArt;
import com.sjiwon.anotherart.global.query.Pagination;

import java.util.List;

public record AuctionArtPagingResponse(
        List<AuctionArt> result,
        Pagination pagination
) {
}

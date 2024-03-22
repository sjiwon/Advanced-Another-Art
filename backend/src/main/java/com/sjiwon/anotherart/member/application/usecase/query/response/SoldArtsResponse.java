package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;

import java.util.List;

public record SoldArtsResponse(
        List<SoldArt> generalArts,
        List<SoldArt> auctionArts
) {
}

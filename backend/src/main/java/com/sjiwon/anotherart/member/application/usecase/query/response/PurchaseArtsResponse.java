package com.sjiwon.anotherart.member.application.usecase.query.response;

import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;

import java.util.List;

public record PurchaseArtsResponse(
        List<PurchaseArt> generalArts,
        List<PurchaseArt> auctionArts
) {
}

package com.sjiwon.anotherart.member.application.usecase.dto;

import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArt;

import java.util.List;

public record PurchaseArtsAssembler(
        List<PurchaseArt> generalArts,
        List<PurchaseArt> auctionArts
) {
}

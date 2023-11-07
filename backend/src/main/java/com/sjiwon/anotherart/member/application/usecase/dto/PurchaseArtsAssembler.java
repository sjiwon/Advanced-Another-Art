package com.sjiwon.anotherart.member.application.usecase.dto;

import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArts;

import java.util.List;

public record PurchaseArtsAssembler(
        List<PurchaseArts> generalArts,
        List<PurchaseArts> auctionArts
) {
}

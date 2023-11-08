package com.sjiwon.anotherart.member.application.usecase.dto;

import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArt;

import java.util.List;

public record SoldArtsAssembler(
        List<SoldArt> generalArts,
        List<SoldArt> auctionArts
) {
}

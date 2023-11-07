package com.sjiwon.anotherart.member.application.usecase.dto;

import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArts;

import java.util.List;

public record SoldArtsAssembler(
        List<SoldArts> generalArts,
        List<SoldArts> auctionArts
) {
}

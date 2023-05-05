package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;

import java.util.List;

public record TradedArtAssembler(
        List<TradedArt> tradedAuctions,
        List<TradedArt> tradedGenerals
) {
}

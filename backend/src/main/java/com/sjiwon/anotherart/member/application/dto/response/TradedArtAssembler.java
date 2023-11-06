package com.sjiwon.anotherart.member.application.dto.response;

import com.sjiwon.anotherart.member.domain.repository.query.dto.response.TradedArt;

import java.util.List;

public record TradedArtAssembler(
        List<TradedArt> tradedAuctions,
        List<TradedArt> tradedGenerals
) {
}

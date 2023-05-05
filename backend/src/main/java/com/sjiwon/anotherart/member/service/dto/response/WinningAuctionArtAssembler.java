package com.sjiwon.anotherart.member.service.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;

import java.util.List;

public record WinningAuctionArtAssembler(List<AuctionArt> result) {
}

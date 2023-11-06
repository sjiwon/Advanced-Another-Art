package com.sjiwon.anotherart.member.application.dto.response;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;

import java.util.List;

public record WinningAuctionArtAssembler(List<AuctionArt> result) {
}

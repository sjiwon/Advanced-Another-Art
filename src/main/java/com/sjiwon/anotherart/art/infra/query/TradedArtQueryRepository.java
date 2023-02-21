package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.SimpleAuctionArt;

import java.util.List;

public interface TradedArtQueryRepository {
    List<SimpleAuctionArt> findSoldAuctionArtListByMemberId(Long memberId);
}

package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.infra.query.dto.SimpleTradedArt;

import java.util.List;

public interface TradedArtQueryRepository {
    List<SimpleTradedArt> findSoldAuctionArtListByMemberId(Long memberId);
    List<SimpleTradedArt> findSoldGeneralArtListByMemberId(Long memberId);
    List<SimpleTradedArt> findPurchaseAuctionArtListByMemberId(Long memberId);
}

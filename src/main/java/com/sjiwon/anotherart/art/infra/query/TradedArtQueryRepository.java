package com.sjiwon.anotherart.art.infra.query;

import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.SimpleTradedArt;

import java.util.List;

public interface TradedArtQueryRepository {
    List<SimpleTradedArt> findSoldArtListByMemberIdAndType(Long ownerId, ArtType type);
    List<SimpleTradedArt> findPurchaseArtListByMemberIdAndType(Long buyerId, ArtType type);
}

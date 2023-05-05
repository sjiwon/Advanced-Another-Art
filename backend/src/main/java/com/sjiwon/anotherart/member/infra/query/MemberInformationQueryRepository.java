package com.sjiwon.anotherart.member.infra.query;

import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;

import java.util.List;

public interface MemberInformationQueryRepository {
    List<MemberPointRecord> findPointRecordByMemberId(Long memberId);
    List<AuctionArt> findWinningAuctionArtByMemberId(Long memberId);
    List<TradedArt> findSoldArtByMemberIdAndType(Long memberId, ArtType type);
    List<TradedArt> findPurchaseArtByMemberIdAndType(Long memberId, ArtType type);
}

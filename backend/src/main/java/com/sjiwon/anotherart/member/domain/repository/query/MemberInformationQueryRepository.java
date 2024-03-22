package com.sjiwon.anotherart.member.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.WinningAuctionArt;

import java.util.List;

public interface MemberInformationQueryRepository {
    MemberInformation fetchInformation(final long memberId);

    List<MemberPointRecord> fetchPointRecords(final long memberId);

    List<WinningAuctionArt> fetchWinningAuctionArts(final long memberId);

    List<SoldArt> fetchSoldArtsByType(final long memberId, final Art.Type type);

    List<PurchaseArt> fetchPurchaseArtsByType(final long memberId, final Art.Type type);
}

package com.sjiwon.anotherart.member.domain.repository.query;

import com.sjiwon.anotherart.art.domain.model.ArtType;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArt;

import java.util.List;

public interface MemberInformationQueryRepository {
    MemberInformation fetchInformation(final long memberId);

    List<MemberPointRecord> fetchPointRecords(final long memberId);

    List<WinningAuctionArt> fetchWinningAuctionArts(final long memberId);

    List<SoldArt> fetchSoldArtsByType(final long memberId, final ArtType type);

    List<PurchaseArt> fetchPurchaseArtsByType(final long memberId, final ArtType type);
}

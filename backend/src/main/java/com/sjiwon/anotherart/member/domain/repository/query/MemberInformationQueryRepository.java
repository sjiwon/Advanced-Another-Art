package com.sjiwon.anotherart.member.domain.repository.query;

import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArts;

import java.util.List;

public interface MemberInformationQueryRepository {
    MemberInformation fetchInformation(final long memberId);

    List<MemberPointRecord> fetchPointRecords(final long memberId);

    List<WinningAuctionArts> fetchWinningAuctionArts(final long memberId);

    List<SoldArts> fetchSoldArtsByType(final long memberId, final ArtType type);

    List<PurchaseArts> fetchPurchaseArtsByType(final long memberId, final ArtType type);
}

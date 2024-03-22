package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.application.usecase.query.response.PurchaseArtsResponse;
import com.sjiwon.anotherart.member.application.usecase.query.response.SoldArtsResponse;
import com.sjiwon.anotherart.member.domain.repository.query.MemberInformationQueryRepository;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.WinningAuctionArt;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.Art.Type.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.Art.Type.GENERAL;

@UseCase
@RequiredArgsConstructor
public class MemberInfoQueryUseCase {
    private final MemberInformationQueryRepository memberInformationRepository;

    public MemberInformation getInformation(final long memberId) {
        return memberInformationRepository.fetchInformation(memberId);
    }

    public List<MemberPointRecord> getPointRecords(final long memberId) {
        return memberInformationRepository.fetchPointRecords(memberId);
    }

    public List<WinningAuctionArt> getWinningAuctionArts(final long memberId) {
        return memberInformationRepository.fetchWinningAuctionArts(memberId);
    }

    public SoldArtsResponse getSoldArts(final long memberId) {
        final List<SoldArt> soldGeneralArts = memberInformationRepository.fetchSoldArtsByType(memberId, GENERAL);
        final List<SoldArt> soldAuctionArts = memberInformationRepository.fetchSoldArtsByType(memberId, AUCTION);
        return new SoldArtsResponse(soldGeneralArts, soldAuctionArts);
    }

    public PurchaseArtsResponse getPurchaseArts(final long memberId) {
        final List<PurchaseArt> purchaseGeneralArts = memberInformationRepository.fetchPurchaseArtsByType(memberId, GENERAL);
        final List<PurchaseArt> purchaseAuctionArts = memberInformationRepository.fetchPurchaseArtsByType(memberId, AUCTION);
        return new PurchaseArtsResponse(purchaseGeneralArts, purchaseAuctionArts);
    }
}

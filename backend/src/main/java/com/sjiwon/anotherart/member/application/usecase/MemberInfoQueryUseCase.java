package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.application.usecase.query.response.MemberInformationResponse;
import com.sjiwon.anotherart.member.application.usecase.query.response.PointRecordResponse;
import com.sjiwon.anotherart.member.application.usecase.query.response.PurchaseArtAssembler;
import com.sjiwon.anotherart.member.application.usecase.query.response.SoldArtAssembler;
import com.sjiwon.anotherart.member.application.usecase.query.response.WinningAuctionArtResponse;
import com.sjiwon.anotherart.member.domain.repository.query.MemberInformationQueryRepository;
import com.sjiwon.anotherart.member.domain.repository.query.response.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.response.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.response.SoldArt;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.Art.Type.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.Art.Type.GENERAL;

@UseCase
@RequiredArgsConstructor
public class MemberInfoQueryUseCase {
    private final MemberInformationQueryRepository memberInformationRepository;

    public MemberInformationResponse getInformation(final long memberId) {
        final MemberInformation result = memberInformationRepository.fetchInformation(memberId);
        return MemberInformationResponse.from(result);
    }

    public List<PointRecordResponse> getPointRecords(final long memberId) {
        return memberInformationRepository.fetchPointRecords(memberId)
                .stream()
                .map(PointRecordResponse::from)
                .toList();
    }

    public List<WinningAuctionArtResponse> getWinningAuctionArts(final long memberId) {
        return memberInformationRepository.fetchWinningAuctionArts(memberId)
                .stream()
                .map(WinningAuctionArtResponse::from)
                .toList();
    }

    public SoldArtAssembler getSoldArts(final long memberId) {
        final List<SoldArt> soldGeneralArts = memberInformationRepository.fetchSoldArtsByType(memberId, GENERAL);
        final List<SoldArt> soldAuctionArts = memberInformationRepository.fetchSoldArtsByType(memberId, AUCTION);
        return SoldArtAssembler.of(soldGeneralArts, soldAuctionArts);
    }

    public PurchaseArtAssembler getPurchaseArts(final long memberId) {
        final List<PurchaseArt> purchaseGeneralArts = memberInformationRepository.fetchPurchaseArtsByType(memberId, GENERAL);
        final List<PurchaseArt> purchaseAuctionArts = memberInformationRepository.fetchPurchaseArtsByType(memberId, AUCTION);
        return PurchaseArtAssembler.of(purchaseGeneralArts, purchaseAuctionArts);
    }
}

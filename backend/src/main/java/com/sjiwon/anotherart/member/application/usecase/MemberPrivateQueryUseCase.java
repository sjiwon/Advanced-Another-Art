package com.sjiwon.anotherart.member.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.member.application.usecase.dto.PurchaseArtsAssembler;
import com.sjiwon.anotherart.member.application.usecase.dto.SoldArtsAssembler;
import com.sjiwon.anotherart.member.application.usecase.query.GetInformationById;
import com.sjiwon.anotherart.member.application.usecase.query.GetPointRecordsById;
import com.sjiwon.anotherart.member.application.usecase.query.GetPurchaseArtsById;
import com.sjiwon.anotherart.member.application.usecase.query.GetSoldArtsById;
import com.sjiwon.anotherart.member.application.usecase.query.GetWinningAuctionArtsById;
import com.sjiwon.anotherart.member.domain.repository.query.MemberInformationQueryRepository;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberInformation;
import com.sjiwon.anotherart.member.domain.repository.query.dto.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArt;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArt;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArt;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;

@UseCase
@RequiredArgsConstructor
public class MemberPrivateQueryUseCase {
    private final MemberInformationQueryRepository memberInformationRepository;

    public MemberInformation getInformation(final GetInformationById query) {
        return memberInformationRepository.fetchInformation(query.memberId());
    }

    public List<MemberPointRecord> getPointRecords(final GetPointRecordsById query) {
        return memberInformationRepository.fetchPointRecords(query.memberId());
    }

    public List<WinningAuctionArt> getWinningAuctionArts(final GetWinningAuctionArtsById query) {
        return memberInformationRepository.fetchWinningAuctionArts(query.memberId());
    }

    public SoldArtsAssembler getSoldArts(final GetSoldArtsById query) {
        final List<SoldArt> soldGeneralArts = memberInformationRepository.fetchSoldArtsByType(query.memberId(), GENERAL);
        final List<SoldArt> soldAuctionArts = memberInformationRepository.fetchSoldArtsByType(query.memberId(), AUCTION);

        return new SoldArtsAssembler(soldGeneralArts, soldAuctionArts);
    }

    public PurchaseArtsAssembler getPurchaseArts(final GetPurchaseArtsById query) {
        final List<PurchaseArt> purchaseGeneralArts = memberInformationRepository.fetchPurchaseArtsByType(query.memberId(), GENERAL);
        final List<PurchaseArt> purchaseAuctionArts = memberInformationRepository.fetchPurchaseArtsByType(query.memberId(), AUCTION);

        return new PurchaseArtsAssembler(purchaseGeneralArts, purchaseAuctionArts);
    }
}

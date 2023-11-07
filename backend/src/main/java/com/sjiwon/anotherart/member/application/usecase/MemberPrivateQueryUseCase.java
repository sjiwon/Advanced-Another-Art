package com.sjiwon.anotherart.member.application.usecase;

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
import com.sjiwon.anotherart.member.domain.repository.query.dto.PurchaseArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.SoldArts;
import com.sjiwon.anotherart.member.domain.repository.query.dto.WinningAuctionArts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;

@Service
@RequiredArgsConstructor
public class MemberPrivateQueryUseCase {
    private final MemberInformationQueryRepository memberInformationRepository;

    public MemberInformation getInformation(final GetInformationById query) {
        return memberInformationRepository.fetchInformation(query.memberId());
    }

    public List<MemberPointRecord> getPointRecords(final GetPointRecordsById query) {
        return memberInformationRepository.fetchPointRecords(query.memberId());
    }

    public List<WinningAuctionArts> getWinningAuctionArts(final GetWinningAuctionArtsById query) {
        return memberInformationRepository.fetchWinningAuctionArts(query.memberId());
    }

    public SoldArtsAssembler getSoldArts(final GetSoldArtsById query) {
        final List<SoldArts> soldGeneralArts = memberInformationRepository.fetchSoldArtsByType(query.memberId(), GENERAL);
        final List<SoldArts> soldAuctionArts = memberInformationRepository.fetchSoldArtsByType(query.memberId(), AUCTION);

        return new SoldArtsAssembler(soldGeneralArts, soldAuctionArts);
    }

    public PurchaseArtsAssembler getPurchaseArts(final GetPurchaseArtsById query) {
        final List<PurchaseArts> purchaseGeneralArts = memberInformationRepository.fetchPurchaseArtsByType(query.memberId(), GENERAL);
        final List<PurchaseArts> purchaseAuctionArts = memberInformationRepository.fetchPurchaseArtsByType(query.memberId(), AUCTION);

        return new PurchaseArtsAssembler(purchaseGeneralArts, purchaseAuctionArts);
    }
}

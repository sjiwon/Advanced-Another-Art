package com.sjiwon.anotherart.member.application;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.member.application.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.application.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.application.dto.response.TradedArtAssembler;
import com.sjiwon.anotherart.member.application.dto.response.WinningAuctionArtAssembler;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.member.domain.repository.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.domain.repository.query.dto.response.TradedArt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationService {
    private final MemberRepository memberRepository;

    public MemberInformation getInformation(final Long memberId) {
        final Member member = memberRepository.getById(memberId);
        return new MemberInformation(member);
    }

    public PointRecordAssembler getPointRecords(final Long memberId) {
        final List<MemberPointRecord> result = memberRepository.findPointRecordByMemberId(memberId);
        return new PointRecordAssembler(result);
    }

    public WinningAuctionArtAssembler getWinningAuctionArts(final Long memberId) {
        final List<AuctionArt> result = memberRepository.findWinningAuctionArtByMemberId(memberId);
        return new WinningAuctionArtAssembler(result);
    }

    public TradedArtAssembler getSoldArts(final Long memberId) {
        final List<TradedArt> tradedAuctions = memberRepository.findSoldArtByMemberIdAndType(memberId, AUCTION);
        final List<TradedArt> tradedGenerals = memberRepository.findSoldArtByMemberIdAndType(memberId, GENERAL);
        return new TradedArtAssembler(tradedAuctions, tradedGenerals);
    }

    public TradedArtAssembler getPurchaseArts(final Long memberId) {
        final List<TradedArt> tradedAuctions = memberRepository.findPurchaseArtByMemberIdAndType(memberId, AUCTION);
        final List<TradedArt> tradedGenerals = memberRepository.findPurchaseArtByMemberIdAndType(memberId, GENERAL);
        return new TradedArtAssembler(tradedAuctions, tradedGenerals);
    }
}

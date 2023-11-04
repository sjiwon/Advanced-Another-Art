package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.service.dto.response.TradedArtAssembler;
import com.sjiwon.anotherart.member.service.dto.response.WinningAuctionArtAssembler;
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
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;

    public MemberInformation getInformation(final Long memberId) {
        final Member member = memberFindService.findById(memberId);
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

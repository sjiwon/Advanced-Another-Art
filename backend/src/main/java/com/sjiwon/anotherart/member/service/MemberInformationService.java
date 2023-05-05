package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.service.dto.response.WinningAuctionArtAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberInformationService {
    private final MemberFindService memberFindService;
    private final MemberRepository memberRepository;

    public MemberInformation getInformation(Long memberId) {
        Member member = memberFindService.findById(memberId);
        return new MemberInformation(member);
    }

    public PointRecordAssembler getPointRecords(Long memberId) {
        List<MemberPointRecord> result = memberRepository.findPointRecordByMemberId(memberId);
        return new PointRecordAssembler(result);
    }

    public WinningAuctionArtAssembler getWinningAuctionArts(Long memberId) {
        List<AuctionArt> result = memberRepository.findWinningAuctionArtByMemberId(memberId);
        return new WinningAuctionArtAssembler(result);
    }
}

package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;
import com.sjiwon.anotherart.art.infra.query.dto.SimpleTradedArt;
import com.sjiwon.anotherart.member.service.dto.response.UserTradedArt;
import com.sjiwon.anotherart.member.service.dto.response.UserWinningAuction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.utils.HashtagAssembler.extractHashtagListByArtId;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberProfileWithArtService {
    private final ArtRepository artRepository;

    public List<UserWinningAuction> getWinningAuction(Long memberId) {
        List<BasicAuctionArt> winningAuctionArtList = artRepository.findWinningAuctionArtListByMemberId(memberId);
        List<HashtagSummary> hashtagSummaryList = artRepository.findHashtagSummaryList();

        return winningAuctionArtList.stream()
                .map(basicAuctionArt ->
                        UserWinningAuction.builder()
                                .art(basicAuctionArt)
                                .hashtags(extractHashtagListByArtId(hashtagSummaryList, basicAuctionArt.getArtId()))
                                .build()
                )
                .toList();
    }

    public List<UserTradedArt> getSoldAuctionArt(Long memberId) {
        List<SimpleTradedArt> soldAuctionArtList = artRepository.findSoldArtListByMemberIdAndType(memberId, AUCTION);
        return assemblingResult(soldAuctionArtList);
    }

    public List<UserTradedArt> getSoldGeneralArt(Long memberId) {
        List<SimpleTradedArt> soldGeneralArtList = artRepository.findSoldArtListByMemberIdAndType(memberId, GENERAL);
        return assemblingResult(soldGeneralArtList);
    }

    public List<UserTradedArt> getPurchaseAuctionArt(Long memberId) {
        List<SimpleTradedArt> purchaseAuctionArtList = artRepository.findPurchaseArtListByMemberIdAndType(memberId, AUCTION);
        return assemblingResult(purchaseAuctionArtList);
    }

    public List<UserTradedArt> getPurchaseGeneralArt(Long memberId) {
        List<SimpleTradedArt> purchaseGeneralArtList = artRepository.findPurchaseArtListByMemberIdAndType(memberId, GENERAL);
        return assemblingResult(purchaseGeneralArtList);
    }

    private List<UserTradedArt> assemblingResult(List<SimpleTradedArt> result) {
        List<HashtagSummary> hashtagSummaryList = artRepository.findHashtagSummaryList();

        return result.stream()
                .map(simpleTradedArt ->
                        UserTradedArt.builder()
                                .art(simpleTradedArt)
                                .hashtags(extractHashtagListByArtId(hashtagSummaryList, simpleTradedArt.getArtId()))
                                .build()
                )
                .toList();
    }
}

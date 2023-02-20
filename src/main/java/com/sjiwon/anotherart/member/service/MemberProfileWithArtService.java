package com.sjiwon.anotherart.member.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;
import com.sjiwon.anotherart.member.service.dto.response.UserWinningAuction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}

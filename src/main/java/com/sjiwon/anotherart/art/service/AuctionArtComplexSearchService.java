package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.AuctionRecordSummary;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.FavoriteSummary;
import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.utils.SearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.utils.SearchCondition.SortType.convertSortType;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuctionArtComplexSearchService {
    private final ArtRepository artRepository;

    public Page<AuctionArt> getCurrentActiveAuctionArtList(String sort, Pageable pageRequest) {
        if (SearchCondition.SortType.isNotSupportedSortType(sort)) {
            return PageableExecutionUtils.getPage(new ArrayList<>(), pageRequest, () -> 0L);
        }

        SearchCondition condition = new SearchCondition(AUCTION, convertSortType(sort), null);
        Page<BasicAuctionArt> result = artRepository.findCurrentActiveAuctionArtList(condition, pageRequest);
        return assemblingPagingResult(result, pageRequest);
    }

    private Page<AuctionArt> assemblingPagingResult(Page<BasicAuctionArt> pagingResult, Pageable pageRequest) {
        List<HashtagSummary> hashtagSummaries = artRepository.findHashtagSummaryList();
        List<FavoriteSummary> favoriteSummaries = artRepository.findFavoriteSummaryList();
        List<AuctionRecordSummary> auctionRecordSummaries = artRepository.findAuctionRecordSummaryList();

        List<AuctionArt> result = pagingResult.stream()
                .map(basicAuctionArt ->
                        AuctionArt.builder()
                                .art(basicAuctionArt)
                                .hashtags(extractHashtagListByArtId(hashtagSummaries, basicAuctionArt.getArtId()))
                                .likeMarkingMembers(extractLikeMarkingMemberListByArtId(favoriteSummaries, basicAuctionArt.getArtId()))
                                .bidCount(extractAuctionBidCountByArtId(auctionRecordSummaries, basicAuctionArt.getArtId()))
                                .build()
                )
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(result, pageRequest, pagingResult::getTotalElements);
    }

    private List<String> extractHashtagListByArtId(List<HashtagSummary> hashtagSummaries, Long artId) {
        return hashtagSummaries.stream()
                .filter(simpleHashtag -> simpleHashtag.getArtId().equals(artId))
                .map(HashtagSummary::getName)
                .collect(Collectors.toList());
    }

    // 각 작품에 대해서 좋아요 등록을 한 사용자 ID 리스트 추출
    private List<Long> extractLikeMarkingMemberListByArtId(List<FavoriteSummary> favoriteSummaries, Long artId) {
        return favoriteSummaries.stream()
                .filter(simpleLikeArt -> simpleLikeArt.getArtId().equals(artId))
                .map(FavoriteSummary::getMemberId)
                .collect(Collectors.toList());
    }

    // 각 경매 작품에 대한 입찰 횟수를 추출
    private int extractAuctionBidCountByArtId(List<AuctionRecordSummary> auctionRecordSummaries, Long artId) {
        return (int) auctionRecordSummaries.stream()
                .filter(simpleAuctionHistory -> simpleAuctionHistory.getArtId().equals(artId))
                .count();
    }
}

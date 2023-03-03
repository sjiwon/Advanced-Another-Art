package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.infra.query.dto.FavoriteSummary;
import com.sjiwon.anotherart.art.infra.query.dto.HashtagSummary;
import com.sjiwon.anotherart.art.service.dto.response.GeneralArt;
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

import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.art.utils.HashtagAssembler.extractHashtagListByArtId;
import static com.sjiwon.anotherart.art.utils.LikeMarkingAssembler.extractLikeMarkingMemberListByArtId;
import static com.sjiwon.anotherart.art.utils.SearchCondition.SortType.convertSortType;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GeneralArtComplexSearchService {
    private final ArtRepository artRepository;

    public Page<GeneralArt> getGeneralArtListByKeyword(String keyword, String sort, Pageable pageRequest) {
        if (SearchCondition.SortType.isNotSupportedSortType(sort)) {
            return PageableExecutionUtils.getPage(new ArrayList<>(), pageRequest, () -> 0L);
        }

        SearchCondition condition = new SearchCondition(GENERAL, convertSortType(sort), keyword);
        Page<BasicGeneralArt> result = artRepository.findGeneralArtListByKeyword(condition, pageRequest);
        return assemblingPagingResult(result, pageRequest);
    }

    public Page<GeneralArt> getGeneralArtListByHashtag(String hashtag, String sort, Pageable pageRequest) {
        if (SearchCondition.SortType.isNotSupportedSortType(sort)) {
            return PageableExecutionUtils.getPage(new ArrayList<>(), pageRequest, () -> 0L);
        }

        SearchCondition condition = new SearchCondition(GENERAL, convertSortType(sort), hashtag);
        Page<BasicGeneralArt> result = artRepository.findGeneralArtListByHashtag(condition, pageRequest);
        return assemblingPagingResult(result, pageRequest);
    }

    private Page<GeneralArt> assemblingPagingResult(Page<BasicGeneralArt> pagingResult, Pageable pageRequest) {
        List<HashtagSummary> hashtagSummaries = artRepository.findHashtagSummaryList();
        List<FavoriteSummary> favoriteSummaries = artRepository.findFavoriteSummaryList();

        List<GeneralArt> result = pagingResult.stream()
                .map(basicGeneralArt ->
                        GeneralArt.builder()
                                .art(basicGeneralArt)
                                .hashtags(extractHashtagListByArtId(hashtagSummaries, basicGeneralArt.getArtId()))
                                .likeMarkingMembers(extractLikeMarkingMemberListByArtId(favoriteSummaries, basicGeneralArt.getArtId()))
                                .build()
                )
                .collect(Collectors.toList());
        return PageableExecutionUtils.getPage(result, pageRequest, pagingResult::getTotalElements);
    }
}

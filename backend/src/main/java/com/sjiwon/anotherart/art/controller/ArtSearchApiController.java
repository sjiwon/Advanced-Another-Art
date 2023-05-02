package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.Pagination;
import com.sjiwon.anotherart.art.controller.dto.request.HashtagSearchArtRequest;
import com.sjiwon.anotherart.art.controller.dto.request.KeywordSearchArtRequest;
import com.sjiwon.anotherart.art.controller.dto.request.MainPageSearchRequest;
import com.sjiwon.anotherart.art.controller.dto.response.PagingArtResponse;
import com.sjiwon.anotherart.art.controller.dto.response.SingleArtResponse;
import com.sjiwon.anotherart.art.service.ArtSimpleSearchService;
import com.sjiwon.anotherart.art.service.AuctionArtComplexSearchService;
import com.sjiwon.anotherart.art.service.GeneralArtComplexSearchService;
import com.sjiwon.anotherart.art.service.dto.response.AbstractArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class ArtSearchApiController {
    private final ArtSimpleSearchService artSimpleSearchService;
    private final AuctionArtComplexSearchService auctionArtComplexSearchService;
    private final GeneralArtComplexSearchService generalArtComplexSearchService;

    @GetMapping("/{artId}")
    public ResponseEntity<SingleArtResponse<AbstractArt>> getSingleArt(@PathVariable Long artId) {
        return ResponseEntity.ok(new SingleArtResponse<>(artSimpleSearchService.getSingleArt(artId)));
    }

    @GetMapping("/main")
    public PagingArtResponse<List<AuctionArt>> getCurrentActiceAuctionArtList(@Valid @ModelAttribute MainPageSearchRequest request) {
        Pageable pageRequest = getDefaultPageRequest(request.page());
        Page<AuctionArt> result = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(request.sort(), pageRequest);

        return assemblingResult(result, request.page());
    }

    @GetMapping("/keyword")
    public <T extends AbstractArt> PagingArtResponse<List<T>> getArtListByKeyword(@Valid @ModelAttribute KeywordSearchArtRequest request) {
        Pageable pageRequest = getDefaultPageRequest(request.page());
        Page<T> result = request.isAuctionType()
                ? (Page<T>) auctionArtComplexSearchService.getAuctionArtListByKeyword(request.keyword(), request.sort(), pageRequest)
                : (Page<T>) generalArtComplexSearchService.getGeneralArtListByKeyword(request.keyword(), request.sort(), pageRequest);

        return assemblingResult(result, request.page());
    }

    @GetMapping("/hashtag")
    public <T extends AbstractArt> PagingArtResponse<List<T>> getArtListByHashtag(@Valid @ModelAttribute HashtagSearchArtRequest request) {
        Pageable pageRequest = getDefaultPageRequest(request.page());
        Page<T> result = request.isAuctionType()
                ? (Page<T>) auctionArtComplexSearchService.getAuctionArtListByHashtag(request.hashtag(), request.sort(), pageRequest)
                : (Page<T>) generalArtComplexSearchService.getGeneralArtListByHashtag(request.hashtag(), request.sort(), pageRequest);

        return assemblingResult(result, request.page());
    }

    private Pageable getDefaultPageRequest(int page) {
        return PageRequest.of(page - 1, Pagination.SLICE_PER_PAGE);
    }

    private <T extends AbstractArt> PagingArtResponse<List<T>> assemblingResult(Page<T> result, int currentPage) {
        Pagination pagination = Pagination.builder()
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .currentPage(currentPage)
                .build();

        return new PagingArtResponse<>(result.getContent().size(), result.getContent(), pagination);
    }
}

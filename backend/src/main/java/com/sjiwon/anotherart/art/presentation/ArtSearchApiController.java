package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.dto.response.AuctionArtPagingResponse;
import com.sjiwon.anotherart.art.application.dto.response.GeneralArtPagingResponse;
import com.sjiwon.anotherart.art.application.usecase.ArtQueryUseCase;
import com.sjiwon.anotherart.art.application.usecase.ArtSearchUseCase;
import com.sjiwon.anotherart.art.application.usecase.query.GetActiveAuctionArts;
import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByHashtag;
import com.sjiwon.anotherart.art.application.usecase.query.GetArtsByKeyword;
import com.sjiwon.anotherart.art.domain.repository.query.dto.ArtDetails;
import com.sjiwon.anotherart.art.presentation.dto.request.ArtBasicSearchRequest;
import com.sjiwon.anotherart.art.presentation.dto.request.ArtHashtagSearchRequest;
import com.sjiwon.anotherart.art.presentation.dto.request.ArtKeywordSearchRequest;
import com.sjiwon.anotherart.art.utils.search.ActiveAuctionArtsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.ArtDetailsSearchCondition;
import com.sjiwon.anotherart.art.utils.search.Pagination;
import com.sjiwon.anotherart.art.utils.search.SearchSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.sjiwon.anotherart.art.utils.search.PagingConstants.getPageable;
import static com.sjiwon.anotherart.art.utils.search.SearchSortType.isNotSupportedSortType;

@Tag(name = "작품 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class ArtSearchApiController {
    private final ArtQueryUseCase artQueryUseCase;
    private final ArtSearchUseCase artSearchUseCase;

    @Operation(summary = "작품 단건 조회 Endpoint")
    @GetMapping("/{artId}")
    public ResponseEntity<ArtDetails> getArt(@PathVariable final Long artId) {
        final ArtDetails response = artQueryUseCase.getArtById(artId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "현재 경매 진행중인 작품 조회 Endpoint")
    @GetMapping("/active-auction")
    public ResponseEntity<AuctionArtPagingResponse> getActiveAuctionArts(@ModelAttribute @Valid final ArtBasicSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new AuctionArtPagingResponse(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ActiveAuctionArtsSearchCondition condition = new ActiveAuctionArtsSearchCondition(SearchSortType.from(request.sortType()));
        final AuctionArtPagingResponse response = artSearchUseCase.getActiveAuctionArts(new GetActiveAuctionArts(condition, getPageable(request.page())));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키워드 기반 경매 작품 조회 Endpoint")
    @GetMapping("/auction/keyword")
    public ResponseEntity<AuctionArtPagingResponse> getAuctionArtsByKeyword(@ModelAttribute @Valid final ArtKeywordSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new AuctionArtPagingResponse(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtDetailsSearchCondition condition = new ArtDetailsSearchCondition(SearchSortType.from(request.sortType()), request.keyword());
        final AuctionArtPagingResponse response = artSearchUseCase.getAuctionArtsByKeyword(new GetArtsByKeyword(condition, getPageable(request.page())));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키워드 기반 일반 작품 조회 Endpoint")
    @GetMapping("/general/keyword")
    public ResponseEntity<GeneralArtPagingResponse> getGeneralArtsByKeyword(@ModelAttribute @Valid final ArtKeywordSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new GeneralArtPagingResponse(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtDetailsSearchCondition condition = new ArtDetailsSearchCondition(SearchSortType.from(request.sortType()), request.keyword());
        final GeneralArtPagingResponse response = artSearchUseCase.getGeneralArtsByKeyword(new GetArtsByKeyword(condition, getPageable(request.page())));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "해시태그(객체 탐지) 기반 경매 작품 조회 Endpoint")
    @GetMapping("/auction/hashtag")
    public ResponseEntity<AuctionArtPagingResponse> getAuctionArtsByHashtag(@ModelAttribute @Valid final ArtHashtagSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new AuctionArtPagingResponse(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtDetailsSearchCondition condition = new ArtDetailsSearchCondition(SearchSortType.from(request.sortType()), request.hashtag());
        final AuctionArtPagingResponse response = artSearchUseCase.getAuctionArtsByHashtag(new GetArtsByHashtag(condition, getPageable(request.page())));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "해시태그(객체 탐지) 기반 일반 작품 조회 Endpoint")
    @GetMapping("/general/hashtag")
    public ResponseEntity<GeneralArtPagingResponse> getGeneralArtsByHashtag(@ModelAttribute @Valid final ArtHashtagSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new GeneralArtPagingResponse(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtDetailsSearchCondition condition = new ArtDetailsSearchCondition(SearchSortType.from(request.sortType()), request.hashtag());
        final GeneralArtPagingResponse response = artSearchUseCase.getGeneralArtsByHashtag(new GetArtsByHashtag(condition, getPageable(request.page())));
        return ResponseEntity.ok(response);
    }
}

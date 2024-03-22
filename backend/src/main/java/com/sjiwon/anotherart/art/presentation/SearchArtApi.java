package com.sjiwon.anotherart.art.presentation;

import com.sjiwon.anotherart.art.application.usecase.ArtQueryUseCase;
import com.sjiwon.anotherart.art.application.usecase.ArtSearchUseCase;
import com.sjiwon.anotherart.art.application.usecase.query.response.ArtResponse;
import com.sjiwon.anotherart.art.application.usecase.query.response.AuctionArtResponse;
import com.sjiwon.anotherart.art.application.usecase.query.response.GeneralArtResponse;
import com.sjiwon.anotherart.art.presentation.request.ArtBasicSearchRequest;
import com.sjiwon.anotherart.art.presentation.request.ArtHashtagSearchRequest;
import com.sjiwon.anotherart.art.presentation.request.ArtKeywordSearchRequest;
import com.sjiwon.anotherart.global.query.PageResponse;
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

@Tag(name = "작품 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class SearchArtApi {
    private final ArtQueryUseCase artQueryUseCase;
    private final ArtSearchUseCase artSearchUseCase;

    @Operation(summary = "작품 단건 조회 Endpoint")
    @GetMapping("/{artId}")
    public ResponseEntity<ArtResponse> getArt(@PathVariable final Long artId) {
        final ArtResponse response = artQueryUseCase.getArtById(artId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "현재 진행중인 경매 작품 리스트 조회 Endpoint")
    @GetMapping("/active-auction")
    public ResponseEntity<PageResponse<List<AuctionArtResponse>>> getActiveAuctionArts(
            @ModelAttribute @Valid final ArtBasicSearchRequest request
    ) {
        final PageResponse<List<AuctionArtResponse>> response = artSearchUseCase.getActiveAuctionArts(request.toQuery());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키워드 기반 경매 작품 리스트 조회 Endpoint")
    @GetMapping("/auction/keyword")
    public ResponseEntity<PageResponse<List<AuctionArtResponse>>> getAuctionArtsByKeyword(
            @ModelAttribute @Valid final ArtKeywordSearchRequest request
    ) {
        final PageResponse<List<AuctionArtResponse>> response = artSearchUseCase.getAuctionArtsByKeyword(request.toQuery());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키워드 기반 일반 작품 리스트 조회 Endpoint")
    @GetMapping("/general/keyword")
    public ResponseEntity<PageResponse<List<GeneralArtResponse>>> getGeneralArtsByKeyword(
            @ModelAttribute @Valid final ArtKeywordSearchRequest request
    ) {
        final PageResponse<List<GeneralArtResponse>> response = artSearchUseCase.getGeneralArtsByKeyword(request.toQuery());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "해시태그(객체 탐지) 기반 경매 작품 리스트 조회 Endpoint")
    @GetMapping("/auction/hashtag")
    public ResponseEntity<PageResponse<List<AuctionArtResponse>>> getAuctionArtsByHashtag(
            @ModelAttribute @Valid final ArtHashtagSearchRequest request
    ) {
        final PageResponse<List<AuctionArtResponse>> response = artSearchUseCase.getAuctionArtsByHashtag(request.toQuery());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "해시태그(객체 탐지) 기반 일반 작품 리스트 조회 Endpoint")
    @GetMapping("/general/hashtag")
    public ResponseEntity<PageResponse<List<GeneralArtResponse>>> getGeneralArtsByHashtag(
            @ModelAttribute @Valid final ArtHashtagSearchRequest request
    ) {
        final PageResponse<List<GeneralArtResponse>> response = artSearchUseCase.getGeneralArtsByHashtag(request.toQuery());
        return ResponseEntity.ok(response);
    }
}

package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtBasicSearchRequest;
import com.sjiwon.anotherart.art.controller.dto.request.ArtHashtagSearchRequest;
import com.sjiwon.anotherart.art.controller.dto.request.ArtKeywordSearchRequest;
import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.service.ArtSearchService;
import com.sjiwon.anotherart.art.service.dto.response.ArtAssembler;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.art.utils.search.Pagination;
import com.sjiwon.anotherart.global.dto.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.sjiwon.anotherart.art.utils.search.PagingConstants.getPageRequest;
import static com.sjiwon.anotherart.art.utils.search.SortType.isNotSupportedSortType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class ArtSearchApiController {
    private final ArtSearchService artSearchService;

    @GetMapping("/{artId}")
    public ResponseEntity<ResponseWrapper<ArtDetails>> getArt(@PathVariable final Long artId) {
        final ArtDetails response = artSearchService.getArt(artId);
        return ResponseEntity.ok(ResponseWrapper.from(response));
    }

    @GetMapping
    public ResponseEntity<ArtAssembler> getActiveAuctionArts(@ModelAttribute @Valid final ArtBasicSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new ArtAssembler(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtAssembler response = artSearchService.getActiveAuctionArts(request.sortType(), getPageRequest(request.page() - 1));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keyword")
    public ResponseEntity<ArtAssembler> getArtsByKeyword(@ModelAttribute @Valid final ArtKeywordSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new ArtAssembler(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtDetailSearchCondition condition = new ArtDetailSearchCondition(request.sortType(), request.artType(), request.keyword());
        final ArtAssembler response = artSearchService.getArtsByKeyword(condition, getPageRequest(request.page() - 1));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hashtag")
    public ResponseEntity<ArtAssembler> getArtsByHashtag(@ModelAttribute @Valid final ArtHashtagSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new ArtAssembler(List.of(), Pagination.getEmptyPage(request.page())));
        }

        final ArtDetailSearchCondition condition = new ArtDetailSearchCondition(request.sortType(), request.artType(), request.hashtag());
        final ArtAssembler response = artSearchService.getArtsByHashtag(condition, getPageRequest(request.page() - 1));
        return ResponseEntity.ok(response);
    }
}

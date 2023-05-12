package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtBasicSearchRequest;
import com.sjiwon.anotherart.art.controller.dto.request.ArtHashtagSearchRequest;
import com.sjiwon.anotherart.art.controller.dto.request.ArtKeywordSearchRequest;
import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.service.ArtSearchService;
import com.sjiwon.anotherart.art.service.dto.response.ArtAssembler;
import com.sjiwon.anotherart.art.utils.search.ArtDetailSearchCondition;
import com.sjiwon.anotherart.art.utils.search.Pagination;
import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.sjiwon.anotherart.art.utils.search.PagingConstants.getPageRequest;
import static com.sjiwon.anotherart.art.utils.search.SortType.isNotSupportedSortType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class ArtSearchApiController {
    private final ArtSearchService artSearchService;

    @GetMapping("/{artId}")
    public ResponseEntity<SimpleReponseWrapper<ArtDetails>> getArt(@PathVariable Long artId) {
        ArtDetails response = artSearchService.getArt(artId);
        return ResponseEntity.ok(new SimpleReponseWrapper<>(response));
    }

    @GetMapping
    public ResponseEntity<ArtAssembler> getActiveAuctionArts(@ModelAttribute @Valid ArtBasicSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new ArtAssembler(List.of(), Pagination.getEmptyPage(request.page())));
        }

        ArtAssembler response = artSearchService.getActiveAuctionArts(request.sortType(), getPageRequest(request.page() - 1));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/keyword")
    public ResponseEntity<ArtAssembler> getArtsByKeyword(@ModelAttribute @Valid ArtKeywordSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new ArtAssembler(List.of(), Pagination.getEmptyPage(request.page())));
        }

        ArtDetailSearchCondition condition = new ArtDetailSearchCondition(request.sortType(), request.artType(), request.keyword());
        ArtAssembler response = artSearchService.getArtsByKeyword(condition, getPageRequest(request.page() - 1));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hashtag")
    public ResponseEntity<ArtAssembler> getArtsByHashtag(@ModelAttribute @Valid ArtHashtagSearchRequest request) {
        if (isNotSupportedSortType(request.sortType())) {
            return ResponseEntity.ok(new ArtAssembler(List.of(), Pagination.getEmptyPage(request.page())));
        }

        ArtDetailSearchCondition condition = new ArtDetailSearchCondition(request.sortType(), request.artType(), request.hashtag());
        ArtAssembler response = artSearchService.getArtsByHashtag(condition, getPageRequest(request.page() - 1));
        return ResponseEntity.ok(response);
    }
}

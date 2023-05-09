package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtBasicSearchRequest;
import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.service.ArtSearchService;
import com.sjiwon.anotherart.art.service.dto.response.ArtAssembler;
import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.sjiwon.anotherart.art.utils.search.PagingConstants.getDefaultPageRequest;

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
        ArtAssembler response = artSearchService.getActiveAuctionArts(
                request.sortType(),
                getDefaultPageRequest(request.page() - 1)
        );
        return ResponseEntity.ok(response);
    }
}

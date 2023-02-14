package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.response.SingleArtResponse;
import com.sjiwon.anotherart.art.service.ArtSearchService;
import com.sjiwon.anotherart.art.service.dto.response.AbstractArt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts")
public class ArtSearchApiController {
    private final ArtSearchService artSearchService;

    @GetMapping("/{artId}")
    public ResponseEntity<SingleArtResponse<AbstractArt>> getSingleArt(@PathVariable Long artId) {
        return ResponseEntity.ok(new SingleArtResponse<>(artSearchService.getSingleArt(artId)));
    }
}

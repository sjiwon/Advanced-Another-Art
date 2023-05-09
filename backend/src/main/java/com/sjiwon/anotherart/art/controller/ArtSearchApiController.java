package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.infra.query.dto.response.ArtDetails;
import com.sjiwon.anotherart.art.service.ArtSearchService;
import com.sjiwon.anotherart.global.dto.SimpleReponseWrapper;
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
    public ResponseEntity<SimpleReponseWrapper<ArtDetails>> getArt(@PathVariable Long artId) {
        ArtDetails response = artSearchService.getArt(artId);
        return ResponseEntity.ok(new SimpleReponseWrapper<>(response));
    }
}

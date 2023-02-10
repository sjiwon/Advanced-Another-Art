package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art/{artId}/like")
public class FavoriteApiController {
    private final FavoriteService favoriteService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> like(@PathVariable Long artId, @ExtractPayload Long memberId) {
        favoriteService.like(artId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<Void> likeCancel(@PathVariable Long artId, @ExtractPayload Long memberId) {
        favoriteService.likeCancel(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}/like")
public class FavoriteApiController {
    private final FavoriteService favoriteService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> addLike(@ExtractPayload Long memberId, @PathVariable Long artId) {
        favoriteService.like(artId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@ExtractPayload Long memberId, @PathVariable Long artId) {
        favoriteService.cancel(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

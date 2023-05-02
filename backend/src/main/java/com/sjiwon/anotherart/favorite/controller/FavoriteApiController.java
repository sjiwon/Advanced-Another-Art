package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.token.utils.ExtractPayloadId;
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
    public ResponseEntity<Void> addLike(@PathVariable Long artId, @ExtractPayloadId Long memberId) {
        favoriteService.addLike(artId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@PathVariable Long artId, @ExtractPayloadId Long memberId) {
        favoriteService.removeLike(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

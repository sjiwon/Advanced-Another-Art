package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}/like")
public class FavoriteApiController {
    private final FavoriteService favoriteService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> addLike(@ExtractPayload final Long memberId, @PathVariable final Long artId) {
        favoriteService.like(artId, memberId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<Void> removeLike(@ExtractPayload final Long memberId, @PathVariable final Long artId) {
        favoriteService.cancel(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

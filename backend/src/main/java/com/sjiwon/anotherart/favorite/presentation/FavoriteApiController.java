package com.sjiwon.anotherart.favorite.presentation;

import com.sjiwon.anotherart.favorite.application.usecase.ManageFavoriteUseCase;
import com.sjiwon.anotherart.favorite.application.usecase.command.CancelArtLikeCommand;
import com.sjiwon.anotherart.favorite.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "작품 좋아요/취소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}/like")
public class FavoriteApiController {
    private final ManageFavoriteUseCase manageFavoriteUseCase;

    @Operation(summary = "작품 좋아요 Endpoint")
    @PostMapping
    public ResponseEntity<Void> likeMarking(
            @ExtractPayload final Long memberId,
            @PathVariable final Long artId
    ) {
        manageFavoriteUseCase.markLike(new MarkArtLikeCommand(memberId, artId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작품 좋아요 취소 Endpoint")
    @DeleteMapping
    public ResponseEntity<Void> likeCancellation(
            @ExtractPayload final Long memberId,
            @PathVariable final Long artId
    ) {
        manageFavoriteUseCase.cancelLike(new CancelArtLikeCommand(memberId, artId));
        return ResponseEntity.noContent().build();
    }
}
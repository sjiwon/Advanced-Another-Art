package com.sjiwon.anotherart.like.presentation;

import com.sjiwon.anotherart.global.annotation.Auth;
import com.sjiwon.anotherart.like.application.usecase.CancelArtLikeUseCase;
import com.sjiwon.anotherart.like.application.usecase.MarkArtLikeUseCase;
import com.sjiwon.anotherart.like.application.usecase.command.CancelArtLikeCommand;
import com.sjiwon.anotherart.like.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.token.domain.model.Authenticated;
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
public class LikeApi {
    private final MarkArtLikeUseCase markArtLikeUseCase;
    private final CancelArtLikeUseCase cancelArtLikeUseCase;

    @Operation(summary = "작품 좋아요 Endpoint")
    @PostMapping
    public ResponseEntity<Void> martArtLike(
            @Auth final Authenticated authenticated,
            @PathVariable final Long artId
    ) {
        markArtLikeUseCase.invoke(new MarkArtLikeCommand(authenticated.id(), artId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작품 좋아요 취소 Endpoint")
    @DeleteMapping
    public ResponseEntity<Void> cancelArtLike(
            @Auth final Authenticated authenticated,
            @PathVariable final Long artId
    ) {
        cancelArtLikeUseCase.invoke(new CancelArtLikeCommand(authenticated.id(), artId));
        return ResponseEntity.noContent().build();
    }
}

package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtModifyRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}")
public class ArtModifyApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER') AND @artOwnerValidator.isArtOwner(#artId, #memberId)")
    @PatchMapping
    public ResponseEntity<Void> update(@ExtractPayload final Long memberId,
                                       @PathVariable final Long artId,
                                       @RequestBody @Valid final ArtModifyRequest request) {
        artService.update(artId, request.name(), request.description(), request.hashtags());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER') AND @artOwnerValidator.isArtOwner(#artId, #memberId)")
    @DeleteMapping
    public ResponseEntity<Void> delete(@ExtractPayload final Long memberId,
                                       @PathVariable final Long artId) {
        artService.delete(artId);
        return ResponseEntity.noContent().build();
    }
}

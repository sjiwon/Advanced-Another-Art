package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtModifyRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/arts/{artId}")
public class ArtModifyApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER') AND @artOwnerChecker.isArtOwner(#memberId, #artId)")
    @PatchMapping
    public ResponseEntity<Void> update(@ExtractPayload Long memberId,
                                       @PathVariable Long artId,
                                       @RequestBody @Valid ArtModifyRequest request) {
        artService.update(artId, request.name(), request.description(), request.hashtags());
        return ResponseEntity.noContent().build();
    }
}

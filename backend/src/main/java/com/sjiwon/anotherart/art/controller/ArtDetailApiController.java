package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;
import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.token.utils.ExtractPayloadId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art")
public class ArtDetailApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/duplicate")
    public ResponseEntity<Void> checkDuplicateArtName(@RequestParam String name) {
        artService.checkDuplicateArtName(name);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{artId}/description")
    public ResponseEntity<Void> changeDescription(@PathVariable Long artId, @Valid @RequestBody ChangeArtDescriptionRequest request) {
        artService.changeDescription(artId, request.changeDescription());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{artId}/hashtag")
    public ResponseEntity<Void> updateArtHashtags(@PathVariable Long artId, @Valid @RequestBody UpdateArtHashtagRequest request) {
        artService.updateArtHashtags(artId, request.getHashtagList());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{artId}")
    public ResponseEntity<Void> deleteArt(@PathVariable Long artId, @ExtractPayloadId Long memberId) {
        artService.deleteArt(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

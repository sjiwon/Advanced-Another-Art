package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;
import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art")
public class ArtDetailApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/duplicate-check", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Void> artNameDuplicateCheck(@RequestParam String artName) {
        artService.artNameDuplicateCheck(artName);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{artId}/description")
    public ResponseEntity<Void> changeDescription(@PathVariable Long artId, @Valid @RequestBody ChangeArtDescriptionRequest request) {
        artService.changeDescription(artId, request.getChangeDescription());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{artId}/hashtags")
    public ResponseEntity<Void> updateHashtags(@PathVariable Long artId, @Valid @RequestBody UpdateArtHashtagRequest request) {
        artService.updateHashtags(artId, request.getHashtagList());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{artId}")
    public ResponseEntity<Void> deleteArt(@PathVariable Long artId, @ExtractPayload Long memberId) {
        artService.deleteArt(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

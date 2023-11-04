package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtDuplicateCheckRequest;
import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.token.utils.ExtractPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art")
public class ArtApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerArt(@ExtractPayload final Long ownerId,
                                            @ModelAttribute @Valid final ArtRegisterRequest request) {
        final Long savedArtId = artService.registerArt(ownerId, request);

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/arts/{id}").build(savedArtId))
                .build();
    }

    @GetMapping("/check-duplicates")
    public ResponseEntity<Void> duplicateCheck(@ModelAttribute @Valid final ArtDuplicateCheckRequest request) {
        artService.duplicateCheck(request.resource(), request.value());
        return ResponseEntity.noContent().build();
    }
}

package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art")
public class ArtApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> registerArt(@ExtractPayload Long memberId, @Valid @ModelAttribute ArtRegisterRequest request) {
        artService.registerArt(memberId, request.isAuctionType() ? request.toAuctionArtDto() : request.toGeneralArtDto());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

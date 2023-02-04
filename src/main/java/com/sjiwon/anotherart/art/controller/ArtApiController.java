package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ArtRegisterRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import com.sjiwon.anotherart.global.annotation.RequiredToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "작품 관련 API")
public class ArtApiController {
    private final ArtService artService;

    @RequiredToken
    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiOperation(value = "작품 등록 API", notes = "작품 등록을 진행하는 API")
    public ResponseEntity<Void> artRegistration(@ExtractPayload Long memberId, @Valid @ModelAttribute ArtRegisterRequest request) {
        artService.artRegistration(memberId, request.isAuctionType() ? request.toAuctionArtDto() : request.toGeneralArtDto());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import com.sjiwon.anotherart.global.annotation.RequiredToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art")
@Api(tags = "작품 정보 관련 API")
public class ArtDetailApiController {
    private final ArtService artService;

    @RequiredToken
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/duplicate-check", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "작품명 중복 체크 API", notes = "작품 등록 과정에서 작품명 중복체크를 진행하는 API")
    public ResponseEntity<Void> artNameDuplicateCheck(@ExtractPayload Long memberId, @RequestParam String artName) {
        artService.artNameDuplicateCheck(artName);
        return ResponseEntity.noContent().build();
    }
}

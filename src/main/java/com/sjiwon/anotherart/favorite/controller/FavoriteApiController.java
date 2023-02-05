package com.sjiwon.anotherart.favorite.controller;

import com.sjiwon.anotherart.favorite.service.FavoriteService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art/{artId}/like")
@Api(tags = "작품 좋아요 관련 API")
public class FavoriteApiController {
    private final FavoriteService favoriteService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ApiOperation(value = "작품 좋아요 등록 API", notes = "사용자가 해당 작품에 대한 좋아요를 등록하기 위한 API")
    public ResponseEntity<Void> likeMarking(@PathVariable Long artId, @ExtractPayload Long memberId) {
        favoriteService.likeMarking(artId, memberId);
        return ResponseEntity.noContent().build();
    }
}

package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.controller.dto.request.ChangeArtDescriptionRequest;
import com.sjiwon.anotherart.art.controller.dto.request.UpdateArtHashtagRequest;
import com.sjiwon.anotherart.art.service.ArtService;
import com.sjiwon.anotherart.global.annotation.ExtractPayload;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/art")
@Api(tags = "작품 정보 관련 API")
public class ArtDetailApiController {
    private final ArtService artService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/duplicate-check", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    @ApiOperation(value = "작품명 중복 체크 API", notes = "작품 등록 과정에서 작품명 중복체크를 진행하는 API")
    public ResponseEntity<Void> artNameDuplicateCheck(@RequestParam String artName) {
        artService.artNameDuplicateCheck(artName);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{artId}/description")
    @ApiOperation(value = "작품 설명 수정 API", notes = "작품 설명을 수정하는 API")
    public ResponseEntity<Void> changeDescription(@PathVariable Long artId, @Valid @RequestBody ChangeArtDescriptionRequest request) {
        artService.changeDescription(artId, request.getChangeDescription());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{artId}/hashtags")
    @ApiOperation(value = "작품 해시태그 수정 API", notes = "작품 해시태그를 수정하는 API")
    public ResponseEntity<Void> updateHashtags(@PathVariable Long artId, @Valid @RequestBody UpdateArtHashtagRequest request) {
        artService.updateHashtags(artId, request.getHashtagList());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{artId}")
    @ApiOperation(value = "작품 삭제 API", notes = "작품을 삭제하는 API")
    public ResponseEntity<Void> deleteArt(@ExtractPayload Long memberId, @PathVariable Long artId) {
        artService.deleteArt(memberId, artId);
        return ResponseEntity.noContent().build();
    }
}

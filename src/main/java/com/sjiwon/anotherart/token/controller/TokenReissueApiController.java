package com.sjiwon.anotherart.token.controller;

import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.token.service.TokenReissueService;
import com.sjiwon.anotherart.token.utils.ExtractToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
@Api(tags = "토큰 재발급 API")
public class TokenReissueApiController {
    private final TokenReissueService tokenReissueService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ApiOperation(value = "토큰 재발급 API", notes = "Authorization Header에 있는 Refresh Token으로 Access Token + Refresh Token 재발급")
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractToken String refreshToken) {
        TokenResponse tokenResponse = tokenReissueService.reissueTokens(refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}

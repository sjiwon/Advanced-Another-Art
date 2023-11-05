package com.sjiwon.anotherart.token.controller;

import com.sjiwon.anotherart.global.resolver.ExtractPayload;
import com.sjiwon.anotherart.global.resolver.ExtractToken;
import com.sjiwon.anotherart.token.service.TokenReissueService;
import com.sjiwon.anotherart.token.service.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token/reissue")
public class TokenReissueApiController {
    private final TokenReissueService tokenReissueService;

    @PostMapping
    public ResponseEntity<TokenResponse> reissueTokens(@ExtractPayload final Long memberId, @ExtractToken final String refreshToken) {
        final TokenResponse tokenResponse = tokenReissueService.reissueTokens(memberId, refreshToken);
        return ResponseEntity.ok(tokenResponse);
    }
}

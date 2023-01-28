package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenReissueService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;

    public TokenResponse reissueTokens(String refreshToken) {
        // RTR -> 사용 가능한 Refresh Token인지 확인 (Redis에 존재하면 사용 가능)
        if (!redisTokenService.isRefreshTokenExists(refreshToken)) {
            throw AnotherArtException.type(AuthErrorCode.INVALID_TOKEN);
        }

        // RTR -> 기존 Refresh Token 제거
        redisTokenService.deleteRefreshToken(refreshToken);

        // Access Token & Refresh Token 발급
        Long memberId = jwtTokenProvider.getPayload(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(memberId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);
        redisTokenService.saveRefreshToken(newRefreshToken, memberId);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}

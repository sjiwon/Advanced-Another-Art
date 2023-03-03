package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenReissueService {
    private final TokenPersistenceService tokenPersistenceService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse reissueTokens(Long memberId, String refreshToken) {
        // 사용자가 보유하고 있는 Refresh Token인지
        if (!tokenPersistenceService.isRefreshTokenExists(memberId, refreshToken)) {
            throw AnotherArtException.type(AuthErrorCode.EXPIRED_OR_POLLUTED_TOKEN);
        }

        // Access Token & Refresh Token 발급
        String role = jwtTokenProvider.getRole(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(memberId, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId, role);

        // RTR 정책에 의해 memberId에 해당하는 사용자가 보유하고 있는 Refresh Token 업데이트
        tokenPersistenceService.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}

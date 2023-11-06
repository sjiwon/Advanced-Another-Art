package com.sjiwon.anotherart.token.domain.service;

import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenIssuer {
    private final TokenProvider tokenProvider;
    private final TokenManager tokenManager;

    public AuthToken provideAuthorityToken(final Long memberId) {
        final String accessToken = tokenProvider.createAccessToken(memberId);
        final String refreshToken = tokenProvider.createRefreshToken(memberId);
        tokenManager.synchronizeRefreshToken(memberId, refreshToken);

        return new AuthToken(accessToken, refreshToken);
    }

    public AuthToken reissueAuthorityToken(final Long memberId) {
        final String newAccessToken = tokenProvider.createAccessToken(memberId);
        final String newRefreshToken = tokenProvider.createRefreshToken(memberId);
        tokenManager.updateRefreshToken(memberId, newRefreshToken);

        return new AuthToken(newAccessToken, newRefreshToken);
    }

    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return tokenManager.isMemberRefreshToken(memberId, refreshToken);
    }

    public void deleteRefreshToken(final Long memberId) {
        tokenManager.deleteRefreshToken(memberId);
    }
}

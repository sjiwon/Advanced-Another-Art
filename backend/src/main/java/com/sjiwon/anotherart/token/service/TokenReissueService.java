package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.token.service.response.TokenResponse;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenReissueService {
    private final TokenManager tokenManager;
    private final TokenProvider tokenProvider;
    private final MemberFindService memberFindService;

    @Transactional
    public TokenResponse reissueTokens(final Long memberId, final String refreshToken) {
        // 사용자가 보유하고 있는 Refresh Token인지
        if (!tokenManager.isRefreshTokenExists(memberId, refreshToken)) {
            throw AnotherArtException.type(AuthErrorCode.INVALID_TOKEN);
        }

        // Access Token & Refresh Token 발급
        final Member member = memberFindService.findById(memberId);
        final String newAccessToken = tokenProvider.createAccessToken(member.getId());
        final String newRefreshToken = tokenProvider.createRefreshToken(member.getId());

        // RTR Policy
        tokenManager.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}

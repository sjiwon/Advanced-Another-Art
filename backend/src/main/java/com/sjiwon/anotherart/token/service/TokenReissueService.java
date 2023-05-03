package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import com.sjiwon.anotherart.token.service.response.TokenResponse;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenReissueService {
    private final TokenManager tokenManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberFindService memberFindService;

    @Transactional
    public TokenResponse reissueTokens(Long memberId, String refreshToken) {
        // 사용자가 보유하고 있는 Refresh Token인지
        if (!tokenManager.isRefreshTokenExists(memberId, refreshToken)) {
            throw AnotherArtException.type(TokenErrorCode.AUTH_INVALID_TOKEN);
        }

        // Access Token & Refresh Token 발급
        Member member = memberFindService.findById(memberId);
        String newAccessToken = jwtTokenProvider.createAccessToken(member.getId());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        // RTR Policy
        tokenManager.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}

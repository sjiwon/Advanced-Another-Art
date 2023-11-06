package com.sjiwon.anotherart.token.application;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.MemberFindService;
import com.sjiwon.anotherart.token.application.response.TokenResponse;
import com.sjiwon.anotherart.token.domain.service.TokenManager;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenReissueService {
    private final TokenManager tokenManager;
    private final TokenProvider tokenProvider;
    private final MemberFindService memberFindService;

    @AnotherArtWritableTransactional
    public TokenResponse reissueTokens(final Long memberId, final String refreshToken) {
        if (!tokenManager.isMemberRefreshToken(memberId, refreshToken)) {
            throw AnotherArtException.type(AuthErrorCode.INVALID_TOKEN);
        }

        final Member member = memberFindService.findById(memberId);
        final String newAccessToken = tokenProvider.createAccessToken(member.getId());
        final String newRefreshToken = tokenProvider.createRefreshToken(member.getId());

        // RTR Policy
        tokenManager.updateRefreshToken(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}

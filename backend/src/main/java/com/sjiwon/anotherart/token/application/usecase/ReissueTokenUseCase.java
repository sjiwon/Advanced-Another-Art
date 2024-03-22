package com.sjiwon.anotherart.token.application.usecase;

import com.sjiwon.anotherart.global.annotation.UseCase;
import com.sjiwon.anotherart.global.security.exception.AuthException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.service.MemberReader;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import com.sjiwon.anotherart.token.domain.service.TokenProvider;
import lombok.RequiredArgsConstructor;

import static com.sjiwon.anotherart.global.security.exception.AuthErrorCode.INVALID_TOKEN;

@UseCase
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final MemberReader memberReader;
    private final TokenProvider tokenProvider;
    private final TokenIssuer tokenIssuer;

    public AuthToken invoke(final String refreshToken) {
        final Member member = memberReader.getById(tokenProvider.getId(refreshToken));
        validateMemberToken(member.getId(), refreshToken);
        return tokenIssuer.reissueAuthorityToken(member.getId(), member.getAuthority());
    }

    private void validateMemberToken(final Long memberId, final String refreshToken) {
        if (isAnonymousRefreshToken(memberId, refreshToken)) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    private boolean isAnonymousRefreshToken(final Long memberId, final String refreshToken) {
        return !tokenIssuer.isMemberRefreshToken(memberId, refreshToken);
    }
}

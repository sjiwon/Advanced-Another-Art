package com.sjiwon.anotherart.token.application.usecase;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.token.application.usecase.command.ReissueTokenCommand;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final TokenIssuer tokenIssuer;

    public AuthToken reissueTokens(final ReissueTokenCommand command) {
        if (isAnonymousRefreshToken(command.memberId(), command.refreshToken())) {
            throw AnotherArtException.type(AuthErrorCode.INVALID_TOKEN);
        }

        return tokenIssuer.reissueAuthorityToken(command.memberId());
    }

    private boolean isAnonymousRefreshToken(final Long memberId, final String refreshToken) {
        return !tokenIssuer.isMemberRefreshToken(memberId, refreshToken);
    }
}

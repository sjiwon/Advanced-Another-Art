package com.sjiwon.anotherart.token.application.usecase;

import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.token.application.usecase.command.ReissueTokenCommand;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import com.sjiwon.anotherart.token.exception.TokenErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueTokenUseCase {
    private final TokenIssuer tokenIssuer;

    public AuthToken reissueTokens(final ReissueTokenCommand command) {
        validateMemberToken(command.memberId(), command.refreshToken());
        return tokenIssuer.reissueAuthorityToken(command.memberId());
    }

    private void validateMemberToken(final Long memberId, final String refreshToken) {
        if (isAnonymousRefreshToken(memberId, refreshToken)) {
            throw AnotherArtException.type(TokenErrorCode.INVALID_TOKEN);
        }
    }

    private boolean isAnonymousRefreshToken(final Long memberId, final String refreshToken) {
        return !tokenIssuer.isMemberRefreshToken(memberId, refreshToken);
    }
}

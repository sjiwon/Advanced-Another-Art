package com.sjiwon.anotherart.token.domain.service;

import com.sjiwon.anotherart.global.annotation.AnotherArtWritableTransactional;
import com.sjiwon.anotherart.token.domain.model.Token;
import com.sjiwon.anotherart.token.domain.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @AnotherArtWritableTransactional
    public void synchronizeRefreshToken(final Long memberId, final String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    @AnotherArtWritableTransactional
    public void updateRefreshToken(final Long memberId, final String newRefreshToken) {
        tokenRepository.updateRefreshToken(memberId, newRefreshToken);
    }

    @AnotherArtWritableTransactional
    public void deleteRefreshToken(final Long memberId) {
        tokenRepository.deleteRefreshToken(memberId);
    }

    public boolean isMemberRefreshToken(final Long memberId, final String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}

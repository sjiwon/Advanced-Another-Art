package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.token.domain.Token;
import com.sjiwon.anotherart.token.domain.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;

    @Transactional
    public void synchronizeRefreshToken(Long memberId, String refreshToken) {
        tokenRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        token -> token.updateRefreshToken(refreshToken),
                        () -> tokenRepository.save(Token.issueRefreshToken(memberId, refreshToken))
                );
    }

    @Transactional
    public void reissueRefreshTokenByRtrPolicy(Long memberId, String newRefreshToken) {
        tokenRepository.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByMemberId(Long memberId) {
        tokenRepository.deleteByMemberId(memberId);
    }

    public boolean isRefreshTokenExists(Long memberId, String refreshToken) {
        return tokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}

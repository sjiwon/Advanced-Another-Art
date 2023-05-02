package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.token.domain.RefreshToken;
import com.sjiwon.anotherart.token.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenPersistenceService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveOrUpdateRefreshToken(Long memberId, String refreshToken) {
        if (refreshTokenRepository.existsByMemberId(memberId)) {
            refreshTokenRepository.reissueRefreshTokenByRtrPolicy(memberId, refreshToken);
        } else {
            refreshTokenRepository.save(RefreshToken.issueRefreshToken(memberId, refreshToken));
        }
    }

    @Transactional
    public void reissueRefreshTokenByRtrPolicy(Long memberId, String newRefreshToken) {
        refreshTokenRepository.reissueRefreshTokenByRtrPolicy(memberId, newRefreshToken);
    }

    @Transactional
    public void deleteRefreshTokenViaMemberId(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }

    public boolean isRefreshTokenExists(Long memberId, String refreshToken) {
        return refreshTokenRepository.existsByMemberIdAndRefreshToken(memberId, refreshToken);
    }
}

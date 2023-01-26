package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.token.domain.RedisRefreshToken;
import com.sjiwon.anotherart.token.domain.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    public void saveRefreshToken(Long memberId, String refreshToken) {
        RedisRefreshToken redisRefreshToken = RedisRefreshToken.builder()
                .memberId(memberId)
                .refreshToken(refreshToken)
                .build();
        redisTokenRepository.save(redisRefreshToken);
    }

    public void deleteRefreshTokenViaMemberId(Long memberId) {
        redisTokenRepository.deleteById(memberId);
    }

    public boolean isRefreshTokenExists(Long memberId) {
        return redisTokenRepository.existsById(memberId);
    }
}

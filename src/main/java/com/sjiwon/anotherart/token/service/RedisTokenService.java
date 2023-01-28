package com.sjiwon.anotherart.token.service;

import com.sjiwon.anotherart.token.domain.RedisRefreshToken;
import com.sjiwon.anotherart.token.domain.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenService {
    private final RedisTokenRepository redisTokenRepository;

    public void saveRefreshToken(String refreshToken, Long memberId) {
        RedisRefreshToken redisRefreshToken = RedisRefreshToken.builder()
                .memberId(memberId)
                .refreshToken(refreshToken)
                .build();
        redisTokenRepository.save(redisRefreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTokenRepository.deleteById(refreshToken);
    }

    public boolean isRefreshTokenExists(String refreshToken) {
        return redisTokenRepository.existsById(refreshToken);
    }
}

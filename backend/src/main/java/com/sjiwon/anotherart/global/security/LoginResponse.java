package com.sjiwon.anotherart.global.security;

public record LoginResponse(
        Long id,
        String nickname,
        String accessToken,
        String refreshToken
) {
}

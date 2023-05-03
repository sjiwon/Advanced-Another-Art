package com.sjiwon.anotherart.global.security;

public record LoginResponse(
        Long memberId,
        String nickname,
        String accessToken,
        String refreshToken
) {
}

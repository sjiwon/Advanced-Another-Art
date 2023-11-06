package com.sjiwon.anotherart.token.domain.model;

public record AuthToken(
        String accessToken,
        String refreshToken
) {
}

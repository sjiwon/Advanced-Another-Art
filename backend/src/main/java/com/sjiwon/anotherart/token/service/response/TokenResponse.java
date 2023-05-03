package com.sjiwon.anotherart.token.service.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}

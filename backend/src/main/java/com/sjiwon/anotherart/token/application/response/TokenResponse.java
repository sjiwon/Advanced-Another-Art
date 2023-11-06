package com.sjiwon.anotherart.token.application.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}

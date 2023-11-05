package com.sjiwon.anotherart.token.utils;

public interface TokenProvider {
    String createAccessToken(final Long memberId);

    String createRefreshToken(final Long memberId);

    Long getId(final String token);

    boolean isTokenValid(final String token);
}

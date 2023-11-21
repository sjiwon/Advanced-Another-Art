package com.sjiwon.anotherart.token.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static org.springframework.boot.web.server.Cookie.SameSite.STRICT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Component
public class TokenResponseWriter {
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer";

    private final long cookieAge;

    public TokenResponseWriter(@Value("${jwt.refresh-token-validity}") final long cookieAge) {
        this.cookieAge = cookieAge;
    }

    public void applyAccessToken(final HttpServletResponse response, final String accessToken) {
        response.setHeader(AUTHORIZATION, String.join(" ", AUTHORIZATION_TOKEN_PREFIX, accessToken));
    }

    public void applyRefreshToken(final HttpServletResponse response, final String refreshToken) {
        final ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .maxAge(cookieAge)
                .sameSite(STRICT.attributeValue())
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.addHeader(SET_COOKIE, cookie.toString());
    }
}

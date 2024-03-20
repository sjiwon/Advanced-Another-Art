package com.sjiwon.anotherart.token.utils;

import com.sjiwon.anotherart.token.domain.model.AuthToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static org.springframework.boot.web.server.Cookie.SameSite.NONE;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

@Component
public class TokenResponseWriter {
    private final long refreshTokenCookieAge;

    public TokenResponseWriter(@Value("${jwt.refresh-token-validity}") final long refreshTokenCookieAge) {
        this.refreshTokenCookieAge = refreshTokenCookieAge;
    }

    public void applyToken(final HttpServletResponse response, final AuthToken token) {
        applyAccessToken(response, token.accessToken());
        applyRefreshToken(response, token.refreshToken());
    }

    private void applyAccessToken(final HttpServletResponse response, final String accessToken) {
        response.setHeader(AuthToken.ACCESS_TOKEN_HEADER, String.join(" ", AuthToken.TOKEN_TYPE, accessToken));
    }

    private void applyRefreshToken(final HttpServletResponse response, final String refreshToken) {
        final ResponseCookie cookie = ResponseCookie.from(AuthToken.REFRESH_TOKEN_HEADER, refreshToken)
                .maxAge(refreshTokenCookieAge)
                .sameSite(NONE.attributeValue())
                .secure(true)
                .httpOnly(true)
                .path("/")
                .build();
        response.setHeader(SET_COOKIE, cookie.toString());
    }
}

package com.sjiwon.anotherart.global.security.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class RefreshTokenUtils {
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final long GMT_TO_KST = 60 * 60 * 9;
    private final long refreshTokenValidityInMilliseconds;

    public RefreshTokenUtils(@Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInMilliseconds) {
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds + GMT_TO_KST;
    }

    public void applyRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = createRefreshTokenCookie(refreshToken);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addCookie(cookie);
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) refreshTokenValidityInMilliseconds);
        return cookie;
    }
}

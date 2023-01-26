package com.sjiwon.anotherart.token.utils;

import com.sjiwon.anotherart.token.service.RedisTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
public class RefreshTokenUtils {
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final long GMT_TO_KST = 60 * 60 * 9;
    private final long refreshTokenValidityInMilliseconds;
    private final RedisTokenService redisTokenService;

    public RefreshTokenUtils(final RedisTokenService redisTokenService,
                             @Value("${jwt.refresh-token-validity}") final long refreshTokenValidityInMilliseconds) {
        this.redisTokenService = redisTokenService;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds + GMT_TO_KST;
    }

    public void applyRefreshTokenInCookieAndRedis(HttpServletResponse response, Long memberId, String refreshToken) {
        Cookie cookie = createRefreshTokenCookie(refreshToken);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addCookie(cookie);
        saveRefreshTokenInRedis(memberId, refreshToken);
    }

    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) refreshTokenValidityInMilliseconds);
        return cookie;
    }

    private void saveRefreshTokenInRedis(Long memberId, String refreshToken) {
        redisTokenService.saveRefreshToken(memberId, refreshToken);
    }
//
//    public String extractRefreshToken(HttpServletRequest request) {
//        return Arrays.stream(request.getCookies())
//                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_KEY))
//                .findFirst()
//                .map(Cookie::getValue)
//                .orElse(null);
//    }
}

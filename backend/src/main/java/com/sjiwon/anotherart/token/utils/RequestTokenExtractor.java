package com.sjiwon.anotherart.token.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

import static com.sjiwon.anotherart.token.utils.TokenResponseWriter.AUTHORIZATION_HEADER_TOKEN_PREFIX;
import static com.sjiwon.anotherart.token.utils.TokenResponseWriter.REFRESH_TOKEN_COOKIE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestTokenExtractor {
    public static Optional<String> extractAccessToken(final HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION);
        if (isEmptyToken(token)) {
            return Optional.empty();
        }
        return checkToken(token.split(" "));
    }

    public static Optional<String> extractRefreshToken(final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }

        final String token = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_COOKIE))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (isEmptyToken(token)) {
            return Optional.empty();
        }
        return Optional.of(token);
    }

    private static boolean isEmptyToken(final String token) {
        return !StringUtils.hasText(token);
    }

    private static Optional<String> checkToken(final String[] parts) {
        if (parts.length == 2 && parts[0].equals(AUTHORIZATION_HEADER_TOKEN_PREFIX)) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }
}

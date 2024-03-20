package com.sjiwon.anotherart.token.utils;

import com.sjiwon.anotherart.token.domain.model.AuthToken;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@NoArgsConstructor(access = PRIVATE)
public class TokenExtractor {
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
                .filter(cookie -> cookie.getName().equals(AuthToken.REFRESH_TOKEN_HEADER))
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
        if (parts.length == 2 && parts[0].equals(AuthToken.TOKEN_TYPE)) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }
}

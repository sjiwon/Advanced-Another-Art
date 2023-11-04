package com.sjiwon.anotherart.token.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {
    private static final String BEARER_TYPE = "Bearer";

    public static Optional<String> extractToken(final HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION);
        if (isEmptyAuthorizationHeader(token)) {
            return Optional.empty();
        }
        return checkToken(token.split(" "));
    }

    private static boolean isEmptyAuthorizationHeader(final String token) {
        return !StringUtils.hasText(token);
    }

    private static Optional<String> checkToken(final String[] parts) {
        if (parts.length == 2 && parts[0].equals(BEARER_TYPE)) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }
}

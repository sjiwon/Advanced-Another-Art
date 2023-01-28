package com.sjiwon.anotherart.token.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    public static String extractAccessToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if (value.startsWith(BEARER_TYPE)) {
                return value.substring(BEARER_TYPE.length() + 1);
            }
        }
        return null;
    }
}

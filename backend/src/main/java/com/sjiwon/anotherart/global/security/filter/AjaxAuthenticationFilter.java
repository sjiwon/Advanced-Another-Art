package com.sjiwon.anotherart.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.LoginRequest;
import com.sjiwon.anotherart.global.security.exception.AnotherArtAuthenticationException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class AjaxAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_AJAX_LOGIN_URL = new AntPathRequestMatcher("/api/login", "POST");
    private final ObjectMapper objectMapper;

    public AjaxAuthenticationFilter(final ObjectMapper objectMapper) {
        super(DEFAULT_AJAX_LOGIN_URL);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException {
        validateContentType(request);

        final LoginRequest loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
        validateLoginRequestData(loginRequest.loginId(), loginRequest.loginPassword());

        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.loginId(),
                loginRequest.loginPassword()
        );
        return this.getAuthenticationManager().authenticate(token);
    }

    private void validateContentType(final HttpServletRequest request) {
        if (isRequestHeaderTypeInvalid(request)) {
            throw AnotherArtAuthenticationException.type(AuthErrorCode.INVALID_AUTH_CONTENT_TYPE);
        }
    }

    private static boolean isRequestHeaderTypeInvalid(final HttpServletRequest request) {
        return request.getHeader("Content-Type") == null || !request.getHeader("Content-Type").contains("application/json");
    }

    private void validateLoginRequestData(final String loginId, final String loginPassword) {
        if (isEmpty(loginId) || isEmpty(loginPassword)) {
            throw AnotherArtAuthenticationException.type(AuthErrorCode.INVALID_AUTH_DATA);
        }
    }

    private boolean isEmpty(final String str) {
        return !StringUtils.hasText(str);
    }
}

package com.sjiwon.anotherart.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.exception.AnotherArtAuthException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.principal.MemberLoginRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AjaxAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_AJAX_LOGIN_URL = new AntPathRequestMatcher("/api/login", "POST");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AjaxAuthenticationFilter() {
        super(DEFAULT_AJAX_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        validateLoginRequestFormat(request, response);
        MemberLoginRequest user = objectMapper.readValue(request.getReader(), MemberLoginRequest.class);
        validateLoginRequestValues(user, response);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getLoginId(), user.getLoginPassword());
        return this.getAuthenticationManager().authenticate(token);
    }

    private void validateLoginRequestFormat(HttpServletRequest request, HttpServletResponse response) {
        if (isRequestHeaderTypeInvalid(request)) {
            throw AnotherArtAuthException.type(AuthErrorCode.INVALID_AUTHENTICATION_REQUEST_FORMAT);
        }
    }

    private static boolean isRequestHeaderTypeInvalid(HttpServletRequest request) {
        return request.getHeader("Content-Type") == null || !Objects.equals(request.getHeader("Content-Type"), "application/json");
    }

    private void validateLoginRequestValues(MemberLoginRequest user, HttpServletResponse response) {
        if (!StringUtils.hasText(user.getLoginId()) || !StringUtils.hasText(user.getLoginPassword())) {
            throw AnotherArtAuthException.type(AuthErrorCode.INVALID_AUTHENTICATION_REQUEST_VALUE);
        }
    }
}

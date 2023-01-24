package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.exception.ErrorResponse;
import com.sjiwon.anotherart.global.security.exception.AnotherArtAuthenticationException;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AjaxAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ErrorResponse errorResponse = null;
        if (exception instanceof UsernameNotFoundException) {
            errorResponse = ErrorResponse.from(MemberErrorCode.MEMBER_NOT_FOUND);
        } else if (exception instanceof BadCredentialsException) {
            errorResponse = ErrorResponse.from(MemberErrorCode.INVALID_PASSWORD);
        } else if (exception instanceof AnotherArtAuthenticationException authException) {
            errorResponse = ErrorResponse.from(authException.getCode());
        }

        response.setStatus(errorResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}

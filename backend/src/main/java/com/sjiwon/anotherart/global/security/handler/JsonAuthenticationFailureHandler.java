package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.exception.ExceptionResponse;
import com.sjiwon.anotherart.global.exception.GlobalExceptionCode;
import com.sjiwon.anotherart.global.security.exception.AnotherArtAuthenticationException;
import com.sjiwon.anotherart.member.exception.MemberExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException exception
    ) throws IOException {
        final ExceptionResponse exceptionResponse = createErrorResponse(exception);
        sendResponse(response, exceptionResponse);
    }

    private static ExceptionResponse createErrorResponse(final AuthenticationException exception) {
        if (exception instanceof final AnotherArtAuthenticationException ex) {
            return ExceptionResponse.from(ex.getCode());
        } else if (exception instanceof UsernameNotFoundException) {
            return ExceptionResponse.from(MemberExceptionCode.MEMBER_NOT_FOUND);
        } else if (exception instanceof BadCredentialsException) {
            return ExceptionResponse.from(MemberExceptionCode.INVALID_PASSWORD);
        } else {
            log.error("{}", exception, exception);
            return ExceptionResponse.from(GlobalExceptionCode.UNEXPECTED_SERVER_ERROR);
        }
    }

    private void sendResponse(final HttpServletResponse response, final ExceptionResponse exceptionResponse) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), exceptionResponse);
    }
}

package com.sjiwon.anotherart.global.security.filter;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class InvalidTokenExceptionTranslationFilter extends OncePerRequestFilter {
    private final AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (final AnotherArtAccessDeniedException ex) {
            accessDeniedHandler.handle(request, response, ex);
        }
    }
}

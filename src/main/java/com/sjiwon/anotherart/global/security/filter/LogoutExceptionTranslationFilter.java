package com.sjiwon.anotherart.global.security.filter;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class LogoutExceptionTranslationFilter extends OncePerRequestFilter {
    private final AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AnotherArtAccessDeniedException ex) {
            accessDeniedHandler.handle(request, response, ex);
        }
    }
}

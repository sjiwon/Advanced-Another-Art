package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class JwtLogoutTokenCheckHandler implements LogoutHandler {
    @Override
    public void logout(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) {
        if (authentication == null) {
            throw AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_PERMISSION);
        }
    }
}

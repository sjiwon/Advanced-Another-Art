package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.token.domain.service.TokenManager;
import com.sjiwon.anotherart.token.utils.AuthorizationExtractor;
import com.sjiwon.anotherart.token.utils.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
    private final TokenProvider tokenProvider;
    private final TokenManager tokenManager;

    @Override
    public void onLogoutSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) {
        removeRefreshToken(request);
        clearSecurityContextHolder();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }

    private void removeRefreshToken(final HttpServletRequest request) {
        final String accessToken = extractAccessToken(request);
        final Long memberId = tokenProvider.getId(accessToken);
        tokenManager.deleteRefreshTokenByMemberId(memberId);
    }

    private String extractAccessToken(final HttpServletRequest request) {
        return AuthorizationExtractor.extractToken(request)
                .orElseThrow(() -> AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_PERMISSION));
    }

    private void clearSecurityContextHolder() {
        SecurityContextHolder.clearContext();
    }
}

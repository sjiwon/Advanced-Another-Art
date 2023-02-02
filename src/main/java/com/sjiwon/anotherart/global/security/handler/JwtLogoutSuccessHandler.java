package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.global.security.exception.AnotherArtAccessDeniedException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.token.service.RedisTokenService;
import com.sjiwon.anotherart.token.utils.AuthorizationExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
    private final RedisTokenService redisTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        removeRefreshTokenInRedis(request);
        clearSecurityContextHolder();

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
    }

    private void removeRefreshTokenInRedis(HttpServletRequest request) {
        String refreshToken = AuthorizationExtractor.extractToken(request);
        validateRefreshToken(refreshToken);
        redisTokenService.deleteRefreshToken(refreshToken);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw AnotherArtAccessDeniedException.type(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private void clearSecurityContextHolder() {
        SecurityContextHolder.clearContext();
    }
}

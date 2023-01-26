package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.handler.response.TokenResponse;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import com.sjiwon.anotherart.token.utils.RefreshTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenUtils refreshTokenUtils;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Long memberId = getMemberIdViaPrincipal(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(memberId);
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        refreshTokenUtils.applyRefreshTokenInCookieAndRedis(response, memberId, refreshToken);
        sendAccessToken(response, accessToken);
    }

    private Long getMemberIdViaPrincipal(Authentication authentication) {
        return ((MemberPrincipal) authentication.getPrincipal()).getUser().getId();
    }

    private void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        objectMapper.writeValue(response.getWriter(), new TokenResponse(accessToken));
    }
}

package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.global.security.principal.MemberAuthDto;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.token.service.TokenPersistenceService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenPersistenceService tokenPersistenceService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        MemberAuthDto member = getPrincipal(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getRole());

        tokenPersistenceService.saveRefreshToken(member.getId(), refreshToken);
        sendAccessTokenAndRefreshToken(response, member.getNickname(), accessToken, refreshToken);
    }

    private MemberAuthDto getPrincipal(Authentication authentication) {
        return ((MemberPrincipal) authentication.getPrincipal()).getUser();
    }

    private void sendAccessTokenAndRefreshToken(HttpServletResponse response, String nickname, String accessToken, String refreshToken) throws IOException {
        objectMapper.writeValue(response.getWriter(), new TokenResponse(nickname, accessToken, refreshToken));
    }
}

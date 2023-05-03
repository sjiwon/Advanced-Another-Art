package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.LoginResponse;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.token.service.TokenManager;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenManager tokenManager;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        MemberPrincipal member = extractMemberPrincipal(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(member.id());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.id());

        tokenManager.synchronizeRefreshToken(member.id(), refreshToken);
        sendAccessTokenAndRefreshToken(response, member, accessToken, refreshToken);
    }

    private MemberPrincipal extractMemberPrincipal(Authentication authentication) {
        return ((MemberPrincipal) authentication.getPrincipal());
    }

    private void sendAccessTokenAndRefreshToken(HttpServletResponse response, MemberPrincipal member, String accessToken, String refreshToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        LoginResponse tokenResponse = new LoginResponse(
                member.id(),
                member.nickname(),
                accessToken,
                refreshToken
        );
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}

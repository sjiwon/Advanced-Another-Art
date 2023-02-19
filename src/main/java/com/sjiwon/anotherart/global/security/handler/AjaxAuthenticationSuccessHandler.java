package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.TokenResponse;
import com.sjiwon.anotherart.global.security.principal.MemberAuthDto;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.token.service.TokenPersistenceService;
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
    private final TokenPersistenceService tokenPersistenceService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        MemberAuthDto member = getPrincipal(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId(), member.getRole());

        tokenPersistenceService.saveOrUpdateRefreshToken(member.getId(), refreshToken);
        sendAccessTokenAndRefreshToken(response, member, accessToken, refreshToken);
    }

    private MemberAuthDto getPrincipal(Authentication authentication) {
        return ((MemberPrincipal) authentication.getPrincipal()).getUser();
    }

    private void sendAccessTokenAndRefreshToken(HttpServletResponse response, MemberAuthDto member, String accessToken, String refreshToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        TokenResponse tokenResponse = TokenResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}

package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.dto.LoginResponse;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenIssuer tokenIssuer;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        final MemberPrincipal member = extractMemberPrincipal(authentication);
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(member.id());
        sendAccessTokenAndRefreshToken(response, member, authToken);
    }

    private MemberPrincipal extractMemberPrincipal(final Authentication authentication) {
        return ((MemberPrincipal) authentication.getPrincipal());
    }

    private void sendAccessTokenAndRefreshToken(
            final HttpServletResponse response,
            final MemberPrincipal member,
            final AuthToken authToken
    ) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        final LoginResponse tokenResponse = new LoginResponse(
                member.id(),
                member.nickname(),
                authToken.accessToken(),
                authToken.refreshToken()
        );
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}

package com.sjiwon.anotherart.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sjiwon.anotherart.global.security.dto.LoginResponse;
import com.sjiwon.anotherart.global.security.principal.MemberPrincipal;
import com.sjiwon.anotherart.token.domain.model.AuthToken;
import com.sjiwon.anotherart.token.domain.service.TokenIssuer;
import com.sjiwon.anotherart.token.utils.TokenResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenIssuer tokenIssuer;
    private final TokenResponseWriter tokenResponseWriter;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Authentication authentication
    ) throws IOException {
        final MemberPrincipal principal = getPrincipal(authentication);
        final AuthToken authToken = tokenIssuer.provideAuthorityToken(principal.id());

        tokenResponseWriter.applyToken(response, authToken);
        sendResponse(response, principal);
    }

    private MemberPrincipal getPrincipal(final Authentication authentication) {
        return (MemberPrincipal) authentication.getPrincipal();
    }

    private void sendResponse(final HttpServletResponse response, final MemberPrincipal principal) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), new LoginResponse(principal.id(), principal.name(), principal.nickname()));
    }
}

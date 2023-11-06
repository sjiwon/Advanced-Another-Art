package com.sjiwon.anotherart.acceptance.security;

import com.sjiwon.anotherart.global.security.dto.LoginRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.sjiwon.anotherart.acceptance.CommonRequestFixture.postRequest;

public class SecurityAcceptanceProcessor {
    public static ValidatableResponse 시큐리티_로그인을_진행한다(final String loginId, final String loginPassword) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/login")
                .build()
                .toUri()
                .getPath();

        final LoginRequest request = new LoginRequest(loginId, loginPassword);

        return postRequest(request, uri);
    }
}

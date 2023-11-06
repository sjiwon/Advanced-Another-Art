package com.sjiwon.anotherart.acceptance.token;

import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.sjiwon.anotherart.acceptance.CommonRequestFixture.postRequest;

public class TokenAcceptanceProcessor {
    public static ValidatableResponse 토큰을_재발급받는다(final String refreshToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/token/reissue")
                .build()
                .toUri()
                .getPath();

        return postRequest(refreshToken, uri);
    }
}

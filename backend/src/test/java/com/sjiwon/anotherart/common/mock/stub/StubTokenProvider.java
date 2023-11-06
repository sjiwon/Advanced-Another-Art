package com.sjiwon.anotherart.common.mock.stub;

import com.sjiwon.anotherart.token.utils.TokenProvider;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.REFRESH_TOKEN;

public class StubTokenProvider implements TokenProvider {
    @Override
    public String createAccessToken(final Long memberId) {
        return ACCESS_TOKEN;
    }

    @Override
    public String createRefreshToken(final Long memberId) {
        return REFRESH_TOKEN;
    }

    @Override
    public Long getId(final String token) {
        return 1L;
    }

    @Override
    public boolean isTokenValid(final String token) {
        return true;
    }
}

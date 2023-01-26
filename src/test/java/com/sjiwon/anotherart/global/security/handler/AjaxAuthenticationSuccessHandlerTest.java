package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.ObjectMapperUtils;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.redis.domain.RedisRefreshToken;
import com.sjiwon.anotherart.global.redis.domain.RedisTokenRepository;
import com.sjiwon.anotherart.global.security.handler.utils.MemberLoginRequestUtils;
import com.sjiwon.anotherart.global.security.principal.MemberLoginRequest;
import com.sjiwon.anotherart.global.security.token.RefreshTokenUtils;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Security [Handler] -> AjaxAuthenticationSuccessHandler 테스트")
class AjaxAuthenticationSuccessHandlerTest extends ControllerTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RedisTokenRepository redisTokenRepository;

    @Autowired
    MockMvc mockMvc;

    private static final String BASE_URL = "/api/login";
    private static final MemberFixture MEMBER = MemberFixture.A;
    private static final String DEFAULT_LOGIN_ID = MEMBER.getLoginId();
    private static final String DEFAULT_LOGIN_PASSWORD = MEMBER.getPassword();
    private static final String REFRESH_TOKEN_KEY = RefreshTokenUtils.REFRESH_TOKEN_KEY;

    @Test
    @DisplayName("로그인을 성공하면 Access Token은 [HTTP Response Body] Refresh Token은 [HttpOnly Cookie & Redis]에 저장된다")
    void test() throws Exception {
        // given
        Member member = createMember();
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest(DEFAULT_LOGIN_ID, DEFAULT_LOGIN_PASSWORD);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .content(ObjectMapperUtils.objectToJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        // then
        ResultActions apiResult = mockMvc.perform(requestBuilder);

        Optional<RedisRefreshToken> refreshToken = redisTokenRepository.findById(member.getId());
        assertThat(refreshToken).isPresent();
        assertThat(refreshToken.get().getMemberId()).isEqualTo(member.getId());

        apiResult
                .andExpect(status().isOk())
                .andExpect(cookie().exists(REFRESH_TOKEN_KEY))
                .andExpect(cookie().httpOnly(REFRESH_TOKEN_KEY, true))
                .andExpect(cookie().secure(REFRESH_TOKEN_KEY, false))
                .andExpect(cookie().value(REFRESH_TOKEN_KEY, refreshToken.get().getRefreshToken()))
                .andExpect(jsonPath("$.accessToken").exists())
                .andDo(print());
    }

    private Member createMember() {
        return memberRepository.save(MEMBER.toMember(PasswordEncoderUtils.getEncoder()));
    }
}
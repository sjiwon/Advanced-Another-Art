package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.token.service.RedisTokenService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Security [Handler] -> JwtLogoutSuccessHandler 테스트")
class JwtLogoutSuccessHandlerTest extends ControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    RedisTokenService redisTokenService;

    private static final String BASE_URL = "/api/logout";
    private static final String BEARER_TOKEN = "Bearer ";
    private static final MemberFixture MEMBER = MemberFixture.A;

    @Test
    @DisplayName("로그아웃은 Authorization 헤더에 Refresh Token을 담아서 요청해야 하고 로그아웃이 성공하면 Redis에 존재하는 해당 Refresh Token이 삭제된다")
    void test() throws Exception {
        // given
        Member member = createMember();
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        redisTokenService.saveRefreshToken(refreshToken, member.getId());
        assertThat(redisTokenService.isRefreshTokenExists(refreshToken)).isTrue();

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
        assertThat(redisTokenService.isRefreshTokenExists(refreshToken)).isFalse();
    }

    private Member createMember() {
        return memberRepository.save(MEMBER.toMember(PasswordEncoderUtils.getEncoder()));
    }
}
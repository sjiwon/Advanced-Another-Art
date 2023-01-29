package com.sjiwon.anotherart.token.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.token.service.RedisTokenService;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Token [Controller Layer] -> TokenReissueApiController 테스트")
@RequiredArgsConstructor
class TokenReissueApiControllerTest extends ControllerTest {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;

    private static final String BASE_URL = "/api/token/reissue";
    private static final String BEARER_TOKEN = "Bearer ";

    @Nested
    @DisplayName("토큰 재발급 테스트 [POST /api/token/reissue]")
    class reissueToken {
        @Test
        @DisplayName("Authorization 헤더에 Refresh Token 없이 토큰 재발급 엔드포인트에 요청을 하면 예외가 발생한다")
        void test1() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            HttpStatus expectedStatus = expectedError.getStatus();
            String expectedMessage = expectedError.getMessage();

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedMessage))
                    .andDo(print());
        }

        @Test
        @DisplayName("만료된 Refresh Token으로 인해 재발급에 실패한다")
        void test2() throws Exception {
            // given
            ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 0L);
            Member member = createMember();
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
            redisTokenService.saveRefreshToken(refreshToken, member.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            HttpStatus expectedStatus = expectedError.getStatus();
            String expectedMessage = expectedError.getMessage();

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedMessage))
                    .andDo(print());
        }
        
        @Test
        @DisplayName("RTR 정책에 의해서 이미 사용한 Refresh Token으로 Access Token + Refresh Token 재발급은 불가능하다")
        void test3() throws Exception {
            // given
            ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 1209600L);
            Member member = createMember();
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getId()); // Redis에 저장하지 않음에 따라 이미 사용했다고 가정
            
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);
            mockMvc.perform(requestBuilder);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            HttpStatus expectedStatus = expectedError.getStatus();
            String expectedMessage = expectedError.getMessage();

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedMessage))
                    .andDo(print());
        }

        @Test
        @DisplayName("Refresh Token으로 Access Token + Refresh Token 재발급에 성공한다")
        void test4() throws Exception {
            // given
            ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 1209600L);
            Member member = createMember();
            String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
            redisTokenService.saveRefreshToken(refreshToken, member.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andDo(print());
        }
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember(PasswordEncoderUtils.getEncoder()));
    }
}
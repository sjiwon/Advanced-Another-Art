package com.sjiwon.anotherart.token.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Token [Controller Layer] -> TokenReissueApiController 테스트")
class TokenReissueApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("토큰 재발급 테스트 [POST /api/token/reissue]")
    class reissueToken {
        private static final String BASE_URL = "/api/token/reissue";

        @Test
        @DisplayName("Authorization 헤더에 Refresh Token 없이 토큰 재발급 엔드포인트에 요청을 하면 예외가 발생한다")
        void test1() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "TokenApi/Reissue/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("만료된 Refresh Token으로 인해 재발급에 실패한다")
        void test2() throws Exception {
            // given
            ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 0L);
            Member member = createMember();

            // when
            final String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
            tokenPersistenceService.saveRefreshToken(member.getId(), refreshToken);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "TokenApi/Reissue/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }
        
        @Test
        @DisplayName("RTR 정책에 의해서 이미 사용한 Refresh Token으로 Access Token + Refresh Token 재발급은 불가능하다")
        void test3() throws Exception {
            // given
            ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 1209600L);
            Member member = createMember();
            
            // when
            final String refreshToken = jwtTokenProvider.createRefreshToken(member.getId()); // DB에 저장하지 않음에 따라 이미 사용했다고 가정
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_TOKEN;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "TokenApi/Reissue/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("Refresh Token으로 Access Token + Refresh Token 재발급에 성공한다")
        void test4() throws Exception {
            // given
            ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 1209600L);
            Member member = createMember();

            // when
            final String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
            tokenPersistenceService.saveRefreshToken(member.getId(), refreshToken);
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").exists())
                    .andExpect(jsonPath("$.refreshToken").exists())
                    .andDo(
                            document(
                                    "TokenApi/Reissue/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Refresh Token")
                                    ),
                                    responseFields(
                                            fieldWithPath("accessToken").description("새로 발급된 Access Token (Expire - 2시간)"),
                                            fieldWithPath("refreshToken").description("새로 발급된 Refresh Token (Expire - 2주)")
                                    )
                            )
                    );
        }
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}
package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Security [Handler] -> JwtLogoutSuccessHandler 테스트")
class JwtLogoutSuccessHandlerTest extends ControllerTest {
    private static final String BASE_URL = "/api/logout";

    @Test
    @DisplayName("Authorization 헤더에 Refresh Token이 없으면 로그아웃에 실패한다")
    void test1() throws Exception {
        // given
        Member member = createMember();
        
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
                                "Security/Logout/Failure",
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
    @DisplayName("Authorization Header에 Refresh Token이 존재하면 로그아웃에 성공하고 DB에 존재하는 Refresh Token이 삭제된다")
    void test2() throws Exception {
        // given
        Member member = createMember();

        // when
        final String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        tokenPersistenceService.saveRefreshToken(member.getId(), refreshToken);
        assertThat(tokenPersistenceService.isRefreshTokenExists(member.getId(), refreshToken)).isTrue();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .header(AUTHORIZATION, BEARER_TOKEN + refreshToken);

        // then
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(
                        document(
                                "Security/Logout/Success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestHeaders(
                                        headerWithName(AUTHORIZATION).description("Refresh Token")
                                )
                        )
                );

        assertThat(tokenPersistenceService.isRefreshTokenExists(member.getId(), refreshToken)).isFalse();
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}
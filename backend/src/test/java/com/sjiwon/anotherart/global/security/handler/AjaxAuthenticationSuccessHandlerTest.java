package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.security.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Security [Handler] -> AjaxAuthenticationSuccessHandler 테스트")
class AjaxAuthenticationSuccessHandlerTest extends ControllerTest {
    private static final String BASE_URL = "/api/login";

    @Test
    @DisplayName("로그인을 성공하면 Access Token & Refresh Token이 발급된다")
    void success() throws Exception {
        // given
        given(jwtTokenProvider.createAccessToken(any())).willReturn(ACCESS_TOKEN);
        given(jwtTokenProvider.createRefreshToken(any())).willReturn(REFRESH_TOKEN);

        // when
        final LoginRequest request = new LoginRequest(MEMBER_A.getLoginId(), MEMBER_A.getPassword());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJson(request));

        // then
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.memberId").exists(),
                        jsonPath("$.nickname").exists(),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                )
                .andDo(
                        document(
                                "Security/Authentication/Success",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("memberId").description("사용자 ID(PK)"),
                                        fieldWithPath("nickname").description("사용자 닉네임"),
                                        fieldWithPath("accessToken").description("발급된 Access Token (Expire - 2시간)"),
                                        fieldWithPath("refreshToken").description("발급된 Refresh Token (Expire - 2주)")
                                )
                        )
                );
    }
}

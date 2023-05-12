package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberPrivacyInformationApiController 테스트")
class MemberPrivacyInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 로그인 아이디 조회 API [GET /api/member/login-id]")
    class changeNickname {
        private static final String BASE_URL = "/api/member/login-id";

        @Test
        @DisplayName("사용자의 로그인 아이디를 조회한다")
        void success() throws Exception {
            // given
            given(memberService.findLoginId(any(), any())).willReturn("user");

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("name", "이름")
                    .param("email", "email@gmail.com");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").exists(),
                            jsonPath("$.result").value("user")
                    )
                    .andDo(
                            document(
                                    "MemberApi/FindLoginId",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("name").description("사용자 이름"),
                                            parameterWithName("email").description("사용자 이메일")
                                    ),
                                    responseFields(
                                            fieldWithPath("result").description("조회한 로그인 아이디")
                                    )
                            )
                    );
        }
    }
}

package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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

    @Nested
    @DisplayName("비밀번호 초기화를 위한 사용자 인증 API [GET /api/member/reset-password/auth]")
    class authForResetPassword {
        private static final String BASE_URL = "/api/member/reset-password/auth";

        @Test
        @DisplayName("사용자 정보가 일치하지 않으면 예외가 발생한다")
        void throwExceptionByMemberNotFound() throws Exception {
            // given
            doThrow(AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND))
                    .when(memberService)
                    .authForResetPassword(any(), any(), any());

            // when
            final AuthForResetPasswordRequest request = new AuthForResetPasswordRequest(
                    "이름",
                    "email@gmail.com",
                    "로그인아이디"
            );
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.MEMBER_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/ResetPassword/Auth/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("email").description("사용자 이메일")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자 인증을 완료한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(memberService)
                    .authForResetPassword(any(), any(), any());

            // when
            final AuthForResetPasswordRequest request = new AuthForResetPasswordRequest(
                    "이름",
                    "email@gmail.com",
                    "로그인아이디"
            );
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/ResetPassword/Auth/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("email").description("사용자 이메일")
                                    )
                            )
                    );
        }
    }
}

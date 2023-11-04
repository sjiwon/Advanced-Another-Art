package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.SecurityTest;
import com.sjiwon.anotherart.global.security.LoginRequest;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Security [Handler] -> AjaxAuthenticationFailureHandler 테스트")
class AjaxAuthenticationFailureHandlerTest extends SecurityTest {
    private static final String BASE_URL = "/api/login";

    @Test
    @DisplayName("로그인 요청 시 아이디/비밀번호 값을 비워서 보냄에 따라 예외가 발생한다")
    void throwExceptionByInvalidAuthData() throws Exception {
        // when
        final LoginRequest request = new LoginRequest("", "");
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJson(request));

        // then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTH_DATA;
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.status").value(expectedError.getStatus().value()),
                        jsonPath("$.errorCode").exists(),
                        jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(expectedError.getMessage())
                )
                .andDo(
                        document(
                                "Security/Authentication/Failure/Case1",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
                                ),
                                getExceptionResponseFiels()
                        )
                );
    }

    @Test
    @DisplayName("로그인 아이디에 해당하는 사용자 정보가 DB에 없음에 따라 예외가 발생한다")
    void throwExceptionByMemberNotFound() throws Exception {
        // when
        final LoginRequest request = new LoginRequest("anonymous", "1234");
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
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
                                "Security/Authentication/Failure/Case2",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
                                ),
                                getExceptionResponseFiels()
                        )
                );
    }

    @Test
    @DisplayName("비밀번호가 사용자 정보와 일치하지 않음에 따라 예외가 발생한다")
    void throwExceptionByInvalidPassword() throws Exception {
        // when
        final LoginRequest request = new LoginRequest("user", "1234");
        final MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(convertObjectToJson(request));

        // then
        final MemberErrorCode expectedError = MemberErrorCode.INVALID_PASSWORD;
        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.status").value(expectedError.getStatus().value()),
                        jsonPath("$.errorCode").exists(),
                        jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(expectedError.getMessage())
                )
                .andDo(
                        document(
                                "Security/Authentication/Failure/Case3",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
                                ),
                                getExceptionResponseFiels()
                        )
                );
    }
}

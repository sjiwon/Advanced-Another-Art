package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.handler.utils.MemberLoginRequestUtils;
import com.sjiwon.anotherart.global.security.principal.MemberLoginRequest;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Security [Handler] -> AjaxAuthenticationFailureHandler 테스트")
class AjaxAuthenticationFailureHandlerTest extends ControllerTest {
    private static final String BASE_URL = "/api/login";
    private static final MemberFixture MEMBER = MemberFixture.A;
    private static final String DEFAULT_LOGIN_ID = MEMBER.getLoginId();
    private static final String WRONG_LOGIN_ID = "fakeid";
    private static final String DEFAULT_LOGIN_PASSWORD = MEMBER.getPassword();
    private static final String WRONG_LOGIN_PASSWORD = "fakepassword123abc!@#";

    @Test
    @DisplayName("Content-Type이 application/json이 아님에 따라 예외가 발생한다")
    void test1() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("loginId", DEFAULT_LOGIN_ID)
                .param("loginPassword", DEFAULT_LOGIN_PASSWORD);

        // when - then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTHENTICATION_REQUEST_FORMAT;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(
                        document(
                                "Security/Authentication/Failure/Case1",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("loginId").description("로그인 아이디"),
                                        parameterWithName("loginPassword").description("로그인 비밀번호")
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
    @DisplayName("로그인 요청 시 아이디/비밀번호 값을 비워서 보냄에 따라 예외가 발생한다")
    void test2() throws Exception {
        // given
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest("", "");

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(ObjectMapperUtils.objectToJson(loginRequest));

        // then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTHENTICATION_REQUEST_VALUE;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(
                        document(
                                "Security/Authentication/Failure/Case2",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
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
    @DisplayName("로그인 아이디에 해당하는 사용자 정보가 DB에 없음에 따라 예외가 발생한다")
    void test3() throws Exception {
        // given
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest(WRONG_LOGIN_ID, DEFAULT_LOGIN_PASSWORD);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(ObjectMapperUtils.objectToJson(loginRequest));

        // then
        final MemberErrorCode expectedError = MemberErrorCode.MEMBER_NOT_FOUND;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(
                        document(
                                "Security/Authentication/Failure/Case3",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
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
    @DisplayName("비밀번호가 사용자 정보와 일치하지 않음에 따라 예외가 발생한다")
    void test4() throws Exception {
        // given
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest(DEFAULT_LOGIN_ID, WRONG_LOGIN_PASSWORD);

        // when
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(ObjectMapperUtils.objectToJson(loginRequest));

        // then
        final MemberErrorCode expectedError = MemberErrorCode.INVALID_PASSWORD;
        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(
                        document(
                                "Security/Authentication/Failure/Case4",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("loginId").description("로그인 아이디"),
                                        fieldWithPath("loginPassword").description("로그인 비밀번호")
                                ),
                                responseFields(
                                        fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                        fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                        fieldWithPath("message").description("예외 메시지")
                                )
                        )
                );
    }
}
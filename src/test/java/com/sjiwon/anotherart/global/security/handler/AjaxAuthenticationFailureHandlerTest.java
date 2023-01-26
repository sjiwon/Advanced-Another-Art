package com.sjiwon.anotherart.global.security.handler;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.ObjectMapperUtils;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.global.security.handler.utils.MemberLoginRequestUtils;
import com.sjiwon.anotherart.global.security.principal.MemberLoginRequest;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Security [Handler] -> AjaxAuthenticationFailureHandler 테스트")
class AjaxAuthenticationFailureHandlerTest extends ControllerTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;

    private static final String BASE_URL = "/api/login";
    private static final MemberFixture MEMBER = MemberFixture.A;
    private static final String DEFAULT_LOGIN_ID = MEMBER.getLoginId();
    private static final String WRONG_LOGIN_ID = "fakeid";
    private static final String DEFAULT_LOGIN_PASSWORD = MEMBER.getPassword();
    private static final String WRONG_LOGIN_PASSWORD = "fakepassword123abc!@#";

    @Test
    @DisplayName("올바르지 않은 인증방식에 의해 예외가 발생한다 -> [Content-Type: application/json]")
    void test1() throws Exception {
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTHENTICATION_REQUEST_FORMAT;
        HttpStatus expectedStatus = expectedError.getStatus();
        String expectedMessage = expectedError.getMessage();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .param("loginId", DEFAULT_LOGIN_ID)
                .param("loginPassword", DEFAULT_LOGIN_PASSWORD)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 요청 시 아이디/비밀번호 값을 비워서 보냄에 따라 예외가 발생한다")
    void test2() throws Exception {
        // given
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest("", "");

        // when - then
        final AuthErrorCode expectedError = AuthErrorCode.INVALID_AUTHENTICATION_REQUEST_VALUE;
        HttpStatus expectedStatus = expectedError.getStatus();
        String expectedMessage = expectedError.getMessage();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .content(ObjectMapperUtils.objectToJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디 정보가 DB에 없음에 따라 예외가 발생한다")
    void test3() throws Exception {
        // given
        createMember();
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest(WRONG_LOGIN_ID, DEFAULT_LOGIN_PASSWORD);

        // when - then
        final MemberErrorCode expectedError = MemberErrorCode.MEMBER_NOT_FOUND;
        HttpStatus expectedStatus = expectedError.getStatus();
        String expectedMessage = expectedError.getMessage();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .content(ObjectMapperUtils.objectToJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andDo(print());
    }
    
    @Test
    @DisplayName("비밀번호가 사용자 정보와 일치하지 않음에 따라 예외가 발생한다")
    void test4() throws Exception {
        // given
        createMember();
        MemberLoginRequest loginRequest = MemberLoginRequestUtils.createRequest(DEFAULT_LOGIN_ID, WRONG_LOGIN_PASSWORD);

        // when - then
        final MemberErrorCode expectedError = MemberErrorCode.INVALID_PASSWORD;
        HttpStatus expectedStatus = expectedError.getStatus();
        String expectedMessage = expectedError.getMessage();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BASE_URL)
                .content(ObjectMapperUtils.objectToJson(loginRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").exists())
                .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                .andExpect(jsonPath("$.errorCode").exists())
                .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(expectedMessage))
                .andDo(print());
    }

    private void createMember() {
        memberRepository.save(MEMBER.toMember(PasswordEncoderUtils.getEncoder()));
    }
}
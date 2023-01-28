package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.ObjectMapperUtils;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.controller.utils.SignUpRequestUtils;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

@DisplayName("Member [Controller Layer] -> MemberApiController 테스트")
class MemberApiControllerTest extends ControllerTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MockMvc mockMvc;

    private static final String BASE_URL = "/api/member";

    @Nested
    @DisplayName("회원가입 테스트 [POST /api/member]")
    class signUp {
        @Test
        @DisplayName("필수 값이 안들어옴에 따라 회원가입에 실패한다")
        void test1() throws Exception {
            // given
            SignUpRequest signUpRequest = SignUpRequestUtils.createEmptyRequest();

            // when - then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            HttpStatus expectedStatus = expectedError.getStatus();
            String expectedMessage = expectedError.getMessage();

            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(signUpRequest));

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
        @DisplayName("중복되는 값에 의해서 회원가입에 실패한다")
        void test2() throws Exception {
            // given
            Member member = createMember();
            SignUpRequest signUpRequest = SignUpRequestUtils.createFailureSignUpRequest(member);

            // when - then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            HttpStatus expectedStatus = expectedError.getStatus();
            String expectedMessage = expectedError.getMessage();

            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(signUpRequest));

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedStatus.value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedStatus.getReasonPhrase()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedMessage))
                    .andDo(print());
        }

        @Test
        @DisplayName("회원가입에 성공한다")
        void test3() throws Exception {
            // given
            SignUpRequest signUpRequest = SignUpRequestUtils.createSuccessSignUpRequest();

            // when - then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(signUpRequest));

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(print());
        }
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember(PasswordEncoderUtils.getEncoder()));
    }
}
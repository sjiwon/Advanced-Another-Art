package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.member.controller.dto.request.DuplicateCheckRequest;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.controller.utils.DuplicateCheckRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.SignUpRequestUtils;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberApiController 테스트")
class MemberApiControllerTest extends ControllerTest {
    private static final Member member = MemberFixture.A.toMember();

    @Nested
    @DisplayName("회원가입 테스트 [POST /api/member]")
    class signUp {
        private static final String BASE_URL = "/api/member";

        @Test
        @DisplayName("회원가입에 필요한 필수 값들이 비어있음에 따라 회원가입에 실패한다")
        void test1() throws Exception {
            // given
            SignUpRequest signUpRequest = SignUpRequestUtils.createEmptyRequest();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(signUpRequest));

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("로그인 비밀번호"),
                                            fieldWithPath("school").description("재학중인 학교"),
                                            fieldWithPath("postcode").description("우편번호 (5자리)"),
                                            fieldWithPath("defaultAddress").description("기본 주소"),
                                            fieldWithPath("detailAddress").description("상세 주소"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("email").description("이메일")
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
        @DisplayName("중복되는 값(닉네임)에 의해서 회원가입에 실패한다")
        void test2() throws Exception {
            // given
            SignUpRequest signUpRequest = SignUpRequestUtils.createFailureSignUpRequest();
            given(memberService.signUp(any())).willThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(signUpRequest));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("로그인 비밀번호"),
                                            fieldWithPath("school").description("재학중인 학교"),
                                            fieldWithPath("postcode").description("우편번호 (5자리)"),
                                            fieldWithPath("defaultAddress").description("기본 주소"),
                                            fieldWithPath("detailAddress").description("상세 주소"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("email").description("이메일")
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
        @DisplayName("회원가입에 성공한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            SignUpRequest signUpRequest = SignUpRequestUtils.createSuccessSignUpRequest();
            given(memberService.signUp(member)).willReturn(memberId);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(signUpRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/SignUp/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("password").description("로그인 비밀번호"),
                                            fieldWithPath("school").description("재학중인 학교"),
                                            fieldWithPath("postcode").description("우편번호 (5자리)"),
                                            fieldWithPath("defaultAddress").description("기본 주소"),
                                            fieldWithPath("detailAddress").description("상세 주소"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("email").description("이메일")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("회원가입 간 중복 체크 테스트 [GET /api/member/duplicate]")
    class duplicateCheckInSignUp {
        private static final String BASE_URL = "/api/member/duplicate";

        @Test
        @DisplayName("닉네임에 대한 중복 체크")
        void test1() throws Exception {
            // given
            final String resource = "nickname";
            final String successValue = "success" + member.getNickname();
            final String failureValue = member.getNickname();

            doNothing()
                    .when(memberService)
                    .duplicateCheck(resource, successValue);

            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                    .when(memberService)
                    .duplicateCheck(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", successValue);

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", failureValue);

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Nickname/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
                                    )
                            )
                    );

            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(failureRequestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Nickname/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
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
        @DisplayName("로그인 아이디에 대한 중복 체크")
        void test2() throws Exception {
            // given
            final String resource = "loginId";
            final String successValue = "success" + member.getLoginId();
            final String failureValue = member.getLoginId();

            doNothing()
                    .when(memberService)
                    .duplicateCheck(resource, successValue);

            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_LOGIN_ID))
                    .when(memberService)
                    .duplicateCheck(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", successValue);

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", failureValue);

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/LoginId/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
                                    )
                            )
                    );

            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_LOGIN_ID;
            mockMvc.perform(failureRequestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/LoginId/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
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
        @DisplayName("전화번호에 대한 중복 체크")
        void test3() throws Exception {
            // given
            final String resource = "phone";
            final String successValue = member.getPhone().replaceAll("0", "9");
            final String failureValue = member.getPhone();

            doNothing()
                    .when(memberService)
                    .duplicateCheck(resource, successValue);

            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_PHONE))
                    .when(memberService)
                    .duplicateCheck(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", successValue);

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", failureValue);

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Phone/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
                                    )
                            )
                    );

            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_PHONE;
            mockMvc.perform(failureRequestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Phone/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
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
        @DisplayName("이메일에 대한 중복 체크")
        void test4() throws Exception {
            // given
            final String resource = "email";
            final String successValue = "success" + member.getEmailValue();
            final String failureValue = member.getEmailValue();

            doNothing()
                    .when(memberService)
                    .duplicateCheck(resource, successValue);

            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_EMAIL))
                    .when(memberService)
                    .duplicateCheck(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", successValue);

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", failureValue);

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Email/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
                                    )
                            )
                    );

            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_EMAIL;
            mockMvc.perform(failureRequestBuilder)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Email/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
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
        @DisplayName("중복 체크 대상이 아닌 필드에 대해서 중복 체크 요청을 보내면 예외가 발생한다")
        void test5() throws Exception {
            // given
            final String resource = "anonymous";
            final String value = "value";

            DuplicateCheckRequest request = DuplicateCheckRequestUtils.createRequest(resource, value);
            doThrow(AnotherArtException.type(GlobalErrorCode.VALIDATION_ERROR))
                    .when(memberService)
                    .duplicateCheck(resource, value);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("resource", resource)
                    .param("value", value);

            // then
            final GlobalErrorCode expectedError = GlobalErrorCode.VALIDATION_ERROR;
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
                                    "MemberApi/DuplicateCheck/AnonymousFailure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("resource").description("중복 체크 타입"),
                                            parameterWithName("value").description("중복 체크 값")
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
}
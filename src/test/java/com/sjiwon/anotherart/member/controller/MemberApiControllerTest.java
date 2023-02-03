package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.GlobalErrorCode;
import com.sjiwon.anotherart.member.controller.dto.request.DuplicateCheckRequest;
import com.sjiwon.anotherart.member.controller.dto.request.SignUpRequest;
import com.sjiwon.anotherart.member.controller.utils.DuplicateCheckRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.SignUpRequestUtils;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberApiController 테스트")
@RequiredArgsConstructor
class MemberApiControllerTest extends ControllerTest {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;

    @Nested
    @DisplayName("회원가입 테스트 [POST /api/member]")
    class signUp {
        private static final String BASE_URL = "/api/member";

        @Test
        @DisplayName("필수 값이 안들어옴에 따라 회원가입에 실패한다")
        void test1() throws Exception {
            // given
            SignUpRequest signUpRequest = SignUpRequestUtils.createEmptyRequest();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
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
            Member member = createMember();
            SignUpRequest signUpRequest = SignUpRequestUtils.createFailureSignUpRequest(member);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
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
            SignUpRequest signUpRequest = SignUpRequestUtils.createSuccessSignUpRequest();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
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

            assertThat(memberRepository.existsByLoginId(signUpRequest.getLoginId())).isTrue();
            assertThat(memberRepository.existsByNickname(signUpRequest.getNickname())).isTrue();
            assertThat(memberRepository.existsByEmail(Email.from(signUpRequest.getEmail()))).isTrue();
        }
    }

    @Nested
    @DisplayName("회원가입 간 중복 체크 테스트 [POST /api/member/duplicate-check]")
    class duplicateCheckInSignUp {
        private static final String BASE_URL = "/api/member/duplicate-check";

        @Test
        @DisplayName("닉네임에 대한 중복 체크")
        void test1() throws Exception {
            // given
            Member member = createMember();
            final String resource = "nickname";
            final String successValue = "success" + member.getNickname();
            final String failureValue = member.getNickname();

            DuplicateCheckRequest successRequest = DuplicateCheckRequestUtils.createRequest(resource, successValue);
            DuplicateCheckRequest failureRequest = DuplicateCheckRequestUtils.createRequest(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(successRequest));

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(failureRequest));

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Nickname/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
            Member member = createMember();
            final String resource = "loginId";
            final String successValue = "success" + member.getLoginId();
            final String failureValue = member.getLoginId();

            DuplicateCheckRequest successRequest = DuplicateCheckRequestUtils.createRequest(resource, successValue);
            DuplicateCheckRequest failureRequest = DuplicateCheckRequestUtils.createRequest(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(successRequest));

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(failureRequest));

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/LoginId/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
            Member member = createMember();
            final String resource = "phone";
            final String successValue = member.getPhone().replaceAll("0", "9");
            final String failureValue = member.getPhone();

            DuplicateCheckRequest successRequest = DuplicateCheckRequestUtils.createRequest(resource, successValue);
            DuplicateCheckRequest failureRequest = DuplicateCheckRequestUtils.createRequest(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(successRequest));

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(failureRequest));

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Phone/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
            Member member = createMember();
            final String resource = "email";
            final String successValue = "success" + member.getEmail().getValue();
            final String failureValue = member.getEmail().getValue();

            DuplicateCheckRequest successRequest = DuplicateCheckRequestUtils.createRequest(resource, successValue);
            DuplicateCheckRequest failureRequest = DuplicateCheckRequestUtils.createRequest(resource, failureValue);

            // when
            MockHttpServletRequestBuilder successRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(successRequest));

            MockHttpServletRequestBuilder failureRequestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(failureRequest));

            // then
            mockMvc.perform(successRequestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/DuplicateCheck/Email/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

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
                                    requestFields(
                                            fieldWithPath("resource").description("중복 체크 타입"),
                                            fieldWithPath("value").description("중복 체크 값")
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

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }
}
package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.utils.AuthForResetPasswordRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.ChangeNicknameRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.ResetPasswordRequestUtils;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberDetailApiController 테스트")
class MemberDetailApiControllerTest extends ControllerTest {
    private static final Member member = MemberFixture.A.toMember();
    private static final String CHANGE_PREFIX = "change_";
    private static final String DUPLICATE_PREFIX = "duplicate_";

    @Nested
    @DisplayName("사용자 닉네임 수정 테스트 [PATCH /api/member/nickname]")
    class changeNickname {
        private static final String BASE_URL = "/api/member/nickname";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            final String changeNickname = CHANGE_PREFIX + member.getNickname();
            ChangeNicknameRequest request = ChangeNicknameRequestUtils.createRequest(changeNickname);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

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
                                    "MemberApi/ChangeNickname/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("changeNickname").description("변경할 닉네임")
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
        @DisplayName("이전에 사용하던 닉네임에 대해서 수정 요청을 보내면 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String changeNickname = member.getNickname();
            doThrow(AnotherArtException.type(MemberErrorCode.NICKNAME_SAME_AS_BEFORE))
                    .when(memberService)
                    .changeNickname(memberId, changeNickname);

            ChangeNicknameRequest request = ChangeNicknameRequestUtils.createRequest(changeNickname);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.NICKNAME_SAME_AS_BEFORE;
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
                                    "MemberApi/ChangeNickname/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeNickname").description("변경할 닉네임")
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
        @DisplayName("타인이 사용하는 닉네임으로 수정 요청을 보내면 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String changeNickname = DUPLICATE_PREFIX + member.getNickname();
            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                    .when(memberService)
                    .changeNickname(memberId, changeNickname);

            ChangeNicknameRequest request = ChangeNicknameRequestUtils.createRequest(changeNickname);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

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
                                    "MemberApi/ChangeNickname/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeNickname").description("변경할 닉네임")
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
        @DisplayName("닉네임 수정에 성공한다")
        void test4() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String changeNickname = CHANGE_PREFIX + member.getNickname();
            doNothing()
                    .when(memberService)
                    .changeNickname(memberId, changeNickname);

            ChangeNicknameRequest request = ChangeNicknameRequestUtils.createRequest(changeNickname);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("changeNickname").description("변경할 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 로그인 아이디 조회 테스트 [GET /api/member/id]")
    class findLoginId {
        private static final String BASE_URL = "/api/member/id";

        @Test
        @DisplayName("요청으로 보낸 [이름, 이메일] 데이터중 이름에 대한 사용자 정보가 없는 경우 예외가 발생한다")
        void test1() throws Exception {
            /// given
            final String name = member.getName() + "diff";
            final String email = member.getEmailValue();
            given(memberService.findLoginId(name, email)).willThrow(AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("name", name)
                    .param("email", email);

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
                                    "MemberApi/FindId/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("name").description("사용자 이름"),
                                            parameterWithName("email").description("사용자 이메일")
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
        @DisplayName("요청으로 보낸 [이름, 이메일] 데이터중 이메일에 대한 사용자 정보가 없는 경우 예외가 발생한다")
        void test2() throws Exception {
            /// given
            final String name = member.getName();
            final String email = "diff" + member.getEmailValue();
            given(memberService.findLoginId(name, email)).willThrow(AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND));

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("name", name)
                    .param("email", email);

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
                                    "MemberApi/FindId/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("name").description("사용자 이름"),
                                            parameterWithName("email").description("사용자 이메일")
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
        @DisplayName("이름, 이메일을 통해서 사용자 로그인 아이디 조회에 성공한다")
        void test3() throws Exception {
            // given
            final String name = member.getName();
            final String email = member.getEmailValue();
            given(memberService.findLoginId(name, email)).willReturn(member.getLoginId());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("name", name)
                    .param("email", email);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(member.getLoginId()))
                    .andDo(
                            document(
                                    "MemberApi/FindId/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("name").description("사용자 이름"),
                                            parameterWithName("email").description("사용자 이메일")
                                    ),
                                    responseFields(
                                            fieldWithPath("result").description("사용자 로그인 아이디")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정을 위한 사용자 인증 테스트 [POST /api/member/reset-password/auth]")
    class authMemberForResetPassword {
        private static final String BASE_URL = "/api/member/reset-password/auth";

        @Test
        @DisplayName("요청으로 보낸 정보와 일치하는 사용자가 존재하지 않음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            final String name = member.getName() + "diff";
            final String loginId = member.getLoginId();
            final String email = member.getEmailValue();
            doThrow(AnotherArtException.type(MemberErrorCode.MEMBER_NOT_FOUND))
                    .when(memberService)
                    .authMemberForPasswordReset(name, loginId, email);

            AuthForResetPasswordRequest request = AuthForResetPasswordRequestUtils.createRequest(name, loginId, email);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

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
                                    "MemberApi/AuthMemberForResetPassword/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("email").description("사용자 이메일")
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
        @DisplayName("사용자 인증에 성공한다")
        void test2() throws Exception {
            // given
            final String name = member.getName();
            final String loginId = member.getLoginId();
            final String email = member.getEmailValue();
            doNothing()
                    .when(memberService)
                    .authMemberForPasswordReset(name, loginId, email);

            AuthForResetPasswordRequest request = AuthForResetPasswordRequestUtils.createRequest(name, loginId, email);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/AuthMemberForResetPassword/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("email").description("사용자 이메일")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정 테스트 [POST /api/member/reset-password]")
    class resetPassword {
        private static final String BASE_URL = "/api/member/reset-password";

        @Test
        @DisplayName("이전과 동일한 비밀번호로 재설정하면 예외가 발생한다")
        void test1() throws Exception {
            // given
            final String loginId = member.getLoginId();
            final String chnagePassword = MemberFixture.A.getPassword();
            doThrow(AnotherArtException.type(MemberErrorCode.PASSWORD_SAME_AS_BEFORE))
                    .when(memberService)
                    .resetPassword(loginId, chnagePassword);

            ResetPasswordRequest request = ResetPasswordRequestUtils.createRequest(loginId, chnagePassword);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.PASSWORD_SAME_AS_BEFORE;
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
                                    "MemberApi/ResetPassword/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("changePassword").description("변경할 비밀번호")
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
        @DisplayName("비밀번호 재설정에 성공한다")
        void test2() throws Exception {
            // given
            final String loginId = member.getLoginId();
            final String chnagePassword = MemberFixture.A.getPassword() + "diff123";
            doNothing()
                    .when(memberService)
                    .resetPassword(loginId, chnagePassword);

            ResetPasswordRequest request = ResetPasswordRequestUtils.createRequest(loginId, chnagePassword);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/ResetPassword/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("changePassword").description("변경할 비밀번호")
                                    )
                            )
                    );
        }
    }
}
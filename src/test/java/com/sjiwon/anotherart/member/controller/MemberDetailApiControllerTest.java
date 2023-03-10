package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangePasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.utils.AuthForResetPasswordRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.ChangeNicknameRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.ChangePasswordRequestUtils;
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

@DisplayName("Member [Controller Layer] -> MemberDetailApiController ?????????")
class MemberDetailApiControllerTest extends ControllerTest {
    private static final Member member = MemberFixture.A.toMember();
    private static final String CHANGE_PREFIX = "change";
    private static final String DUPLICATE_PREFIX = "duplicate";

    @Nested
    @DisplayName("????????? ????????? ?????? ????????? [PATCH /api/member/nickname]")
    class changeNickname {
        private static final String BASE_URL = "/api/member/nickname";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                            fieldWithPath("changeNickname").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("????????? ???????????? ??????????????? ?????? ????????? ????????? ????????? ????????????")
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
                                            fieldWithPath("changeNickname").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("????????? ???????????? ??????????????? ?????? ????????? ????????? ????????? ????????????")
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
                                            fieldWithPath("changeNickname").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("????????? ????????? ????????????")
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
                                            fieldWithPath("changeNickname").description("????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("????????? ???????????? ?????? ????????? [PATCH /api/member/password]")
    class changePassword {
        private static final String BASE_URL = "/api/member/password";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
        void test1() throws Exception {
            // given
            final String changePassword = CHANGE_PREFIX + MemberFixture.A.getPassword();
            ChangePasswordRequest request = ChangePasswordRequestUtils.createRequest(changePassword);

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
                                    "MemberApi/ChangePassword/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("changePassword").description("????????? ????????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("????????? ???????????? ??????????????? ?????? ????????? ????????? ????????? ????????????")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String changePassword = MemberFixture.A.getPassword();
            doThrow(AnotherArtException.type(MemberErrorCode.PASSWORD_SAME_AS_BEFORE))
                    .when(memberService)
                    .changePassword(memberId, changePassword);

            ChangePasswordRequest request = ChangePasswordRequestUtils.createRequest(changePassword);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
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
                                    "MemberApi/ChangePassword/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("changePassword").description("????????? ????????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ????????? ????????????")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            final String changePassword = CHANGE_PREFIX + MemberFixture.A.getPassword();
            doNothing()
                    .when(memberService)
                    .changePassword(memberId, changePassword);

            ChangePasswordRequest request = ChangePasswordRequestUtils.createRequest(changePassword);

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
                                    "MemberApi/ChangePassword/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestFields(
                                            fieldWithPath("changePassword").description("????????? ????????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ????????? ?????? ????????? [GET /api/member/id]")
    class findLoginId {
        private static final String BASE_URL = "/api/member/id";

        @Test
        @DisplayName("???????????? ?????? [??????, ?????????] ???????????? ????????? ?????? ????????? ????????? ?????? ?????? ????????? ????????????")
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
                                            parameterWithName("name").description("????????? ??????"),
                                            parameterWithName("email").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ?????? [??????, ?????????] ???????????? ???????????? ?????? ????????? ????????? ?????? ?????? ????????? ????????????")
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
                                            parameterWithName("name").description("????????? ??????"),
                                            parameterWithName("email").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("??????, ???????????? ????????? ????????? ????????? ????????? ????????? ????????????")
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
                                            parameterWithName("name").description("????????? ??????"),
                                            parameterWithName("email").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("result").description("????????? ????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ???????????? ?????? ????????? ?????? ????????? [POST /api/member/reset-password/auth]")
    class authMemberForResetPassword {
        private static final String BASE_URL = "/api/member/reset-password/auth";

        @Test
        @DisplayName("???????????? ?????? ????????? ???????????? ???????????? ???????????? ????????? ?????? ????????? ????????????")
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
                                            fieldWithPath("name").description("????????? ??????"),
                                            fieldWithPath("loginId").description("????????? ????????? ?????????"),
                                            fieldWithPath("email").description("????????? ?????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("????????? ????????? ????????????")
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
                                            fieldWithPath("name").description("????????? ??????"),
                                            fieldWithPath("loginId").description("????????? ????????? ?????????"),
                                            fieldWithPath("email").description("????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ????????? [POST /api/member/reset-password]")
    class resetPassword {
        private static final String BASE_URL = "/api/member/reset-password";

        @Test
        @DisplayName("????????? ????????? ??????????????? ??????????????? ????????? ????????????")
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
                                            fieldWithPath("loginId").description("????????? ????????? ?????????"),
                                            fieldWithPath("changePassword").description("????????? ????????????")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                            fieldWithPath("errorCode").description("????????? ?????? ??????"),
                                            fieldWithPath("message").description("?????? ?????????")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("???????????? ???????????? ????????????")
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
                                            fieldWithPath("loginId").description("????????? ????????? ?????????"),
                                            fieldWithPath("changePassword").description("????????? ????????????")
                                    )
                            )
                    );
        }
    }
}
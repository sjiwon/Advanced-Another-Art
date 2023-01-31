package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.ObjectMapperUtils;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.controller.dto.request.AuthForResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.dto.request.FindIdRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ResetPasswordRequest;
import com.sjiwon.anotherart.member.controller.utils.AuthForResetPasswordRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.FindIdRequestUtils;
import com.sjiwon.anotherart.member.controller.utils.ResetPasswordRequestUtils;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import com.sjiwon.anotherart.token.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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
@RequiredArgsConstructor
class MemberDetailApiControllerTest extends ControllerTest {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();
    private static final String BEARER_TOKEN = "Bearer ";

    @Nested
    @DisplayName("사용자 닉네임 수정 테스트 [PATCH /api/member/nickname]")
    class changeNickname {
        private static final String BASE_URL = "/api/member/nickname";
        
        @Test
        @DisplayName("이전에 사용하던 닉네임에 대해서 수정 요청을 보내면 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member member = createMemberA();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final String changeNickname = member.getNickname();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("changeNickname", changeNickname)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

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
                                    "MemberApi/ChangeNickname/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("changeNickname").description("변경할 닉네임")
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
        void test2() throws Exception {
            // given
            Member memberA = createMemberA();
            Member memberB = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(memberA.getId());
            final String changeNickname = memberB.getNickname();

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("changeNickname", changeNickname)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

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
                                    "MemberApi/ChangeNickname/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("changeNickname").description("변경할 닉네임")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
            assertThat(memberA.getNickname()).isNotEqualTo(changeNickname);
        }

        @Test
        @DisplayName("닉네임 수정에 성공한다")
        void test3() throws Exception {
            // given
            Member member = createMemberA();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final String changeNickname = member.getNickname() + "hello world";

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("changeNickname", changeNickname)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

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
                                    requestParameters(
                                            parameterWithName("changeNickname").description("변경할 닉네임")
                                    )
                            )
                    );
            assertThat(member.getNickname()).isEqualTo(changeNickname);
        }
    }

    @Nested
    @DisplayName("사용자의 로그인 아이디 조회 테스트 [POST /api/member/find/id]")
    class findLoginId {
        private static final String BASE_URL = "/api/member/find/id";

        @Test
        @DisplayName("요청으로 보낸 이름, 이메일에 대한 사용자 정보가 존재하지 않으면 예외가 발생한다 (이름 불일치)")
        void test1() throws Exception {
            /// given
            Member member = createMemberA();
            FindIdRequest request = FindIdRequestUtils.createRequest(member.getName() + "diff", member.getEmail().getValue());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

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
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
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
        @DisplayName("요청으로 보낸 이름, 이메일에 대한 사용자 정보가 존재하지 않으면 예외가 발생한다 (이메일 불일치)")
        void test2() throws Exception {
            /// given
            Member member = createMemberA();
            FindIdRequest request = FindIdRequestUtils.createRequest(member.getName(), "diff" + member.getEmail().getValue());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

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
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
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
        @DisplayName("이름, 이메일을 통해서 사용자 아이디를 조회한다")
        void test3() throws Exception {
            // given
            Member member = createMemberA();
            FindIdRequest request = FindIdRequestUtils.createRequest(member.getName(), member.getEmail().getValue());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.value").value(member.getLoginId()))
                    .andDo(
                            document(
                                    "MemberApi/FindId/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("email").description("사용자 이메일")
                                    ),
                                    responseFields(
                                            fieldWithPath("value").description("사용자 로그인 아이디")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정을 위한 사용자 인증 테스트 [POST /api/member/reset/password/auth]")
    class authMemberForResetPassword {
        private static final String BASE_URL = "/api/member/reset/password/auth";

        @Test
        @DisplayName("요청으로 보낸 정보와 일치하는 사용자가 존재하지 않음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member member = createMemberA();
            AuthForResetPasswordRequest request = AuthForResetPasswordRequestUtils.createRequest(member.getName() + "diff", member.getLoginId(), member.getEmail().getValue());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

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
        @DisplayName("인증을 성공적으로 완료한다")
        void test2() throws Exception {
            // given
            Member member = createMemberA();
            AuthForResetPasswordRequest request = AuthForResetPasswordRequestUtils.createRequest(member.getName(), member.getLoginId(), member.getEmail().getValue());

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

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
    @DisplayName("비밀번호 재설정 테스트 [POST /api/member/reset/password]")
    class resetPassword {
        private static final String BASE_URL = "/api/member/reset/password";

        @Test
        @DisplayName("이전과 동일한 비밀번호로 재설정하면 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member member = createMemberA();
            String loginId = member.getLoginId();
            String chnagePassword = MemberFixture.A.getPassword();
            ResetPasswordRequest request = ResetPasswordRequestUtils.createRequest(loginId, chnagePassword);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

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
            Member member = createMemberA();
            String loginId = member.getLoginId();
            String chnagePassword = MemberFixture.A.getPassword() + "456";
            ResetPasswordRequest request = ResetPasswordRequestUtils.createRequest(loginId, chnagePassword);

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .content(ObjectMapperUtils.objectToJson(request))
                    .contentType(MediaType.APPLICATION_JSON);

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
            assertThat(ENCODER.matches(chnagePassword, member.getPassword().getValue())).isTrue();
        }
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember(ENCODER));
    }


    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember(ENCODER));
    }
}
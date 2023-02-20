package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberProfileApiController 테스트")
class MemberProfileApiControllerTest extends ControllerTest {
    private static final String BEARER = "Bearer ";
    private static final Member DEFAULT_MEMBER = MemberFixture.A.toMember();
    private static final int DEFAULT_TOTAL_POINT = 100_000_000;

    @Nested
    @DisplayName("사용자 프로필 정보 조회 테스트 [GET /api/members/{memberId}]")
    class userProfile {
        private static final String BASE_URL = "/api/members/{memberId}";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId);

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
                                    "MemberProfileApi/UserProfile/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
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
        @DisplayName("Access Token Payload의 memberId와 PathVariable의 memberId가 일치하지 않음에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 2L;

            final String accessToken = jwtTokenProvider.createAccessToken(payloadId, ROLE_USER);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER + accessToken);

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
                                    "MemberProfileApi/UserProfile/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
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
        @DisplayName("x")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;

            setMemberId();
            UserProfile response = UserProfile.builder()
                    .member(DEFAULT_MEMBER)
                    .totalPoint(DEFAULT_TOTAL_POINT)
                    .build();
            given(memberProfileService.getUserProfile(memberId)).willReturn(response);

            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").exists())
                    .andExpect(jsonPath("$.nickname").exists())
                    .andExpect(jsonPath("$.loginId").exists())
                    .andExpect(jsonPath("$.email").exists())
                    .andExpect(jsonPath("$.school").exists())
                    .andExpect(jsonPath("$.phone").exists())
                    .andExpect(jsonPath("$.address").exists())
                    .andExpect(jsonPath("$.availablePoint").exists())
                    .andExpect(jsonPath("$.totalPoint").exists())
                    .andDo(
                            document(
                                    "MemberProfileApi/UserProfile/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID(PK)"),
                                            fieldWithPath("name").description("이름"),
                                            fieldWithPath("nickname").description("닉네임"),
                                            fieldWithPath("loginId").description("로그인 아이디"),
                                            fieldWithPath("email").description("이메일"),
                                            fieldWithPath("school").description("재학중인 학교명"),
                                            fieldWithPath("phone").description("전화번호"),
                                            fieldWithPath("address.postcode").description("우편번호"),
                                            fieldWithPath("address.defaultAddress").description("기본 주소"),
                                            fieldWithPath("address.detailAddress").description("상세 주소"),
                                            fieldWithPath("availablePoint").description("사용 가능한 포인트"),
                                            fieldWithPath("totalPoint").description("전체 보유 포인트")
                                    )
                            )
                    );
        }
    }

    private void setMemberId() {
        ReflectionTestUtils.setField(DEFAULT_MEMBER, "id", 1L);
    }
}
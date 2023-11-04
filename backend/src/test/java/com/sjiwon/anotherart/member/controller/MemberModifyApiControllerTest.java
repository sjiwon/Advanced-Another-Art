package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeAddressRequest;
import com.sjiwon.anotherart.member.controller.dto.request.ChangeNicknameRequest;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberModifyApiController 테스트")
class MemberModifyApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("닉네임 변경 API [PATCH /api/members/{memberId}/nickname] - AccessToken 필수")
    class changeNickname {
        private static final String BASE_URL = "/api/members/{memberId}/nickname";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("타인이 사용하고 있는 닉네임으로 변경할 수 없다")
        void throwExceptionByDuplicateNickname() throws Exception {
            // given
            doThrow(AnotherArtException.type(MemberErrorCode.DUPLICATE_NICKNAME))
                    .when(memberService)
                    .changeNickname(any(), any());

            // when
            final ChangeNicknameRequest request = new ChangeNicknameRequest("update");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.DUPLICATE_NICKNAME;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("value").description("변경할 닉네임")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @WithMockUser
        @DisplayName("이전과 동일한 닉네임으로 변경할 수 없다")
        void throwExceptionByNicknameSameAsBefore() throws Exception {
            // given
            doThrow(AnotherArtException.type(MemberErrorCode.NICKNAME_SAME_AS_BEFORE))
                    .when(memberService)
                    .changeNickname(any(), any());

            // when
            final ChangeNicknameRequest request = new ChangeNicknameRequest("update");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.NICKNAME_SAME_AS_BEFORE;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isConflict(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("value").description("변경할 닉네임")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @WithMockUser
        @DisplayName("닉네임 변경을 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(memberService)
                    .changeNickname(any(), any());

            // when
            final ChangeNicknameRequest request = new ChangeNicknameRequest("update");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/ChangeNickname/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("value").description("변경할 닉네임")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("주소 변경 API [PATCH /api/members/{memberId}/address] - AccessToken 필수")
    class changeAddress {
        private static final String BASE_URL = "/api/members/{memberId}/address";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("주소 변경을 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(memberService)
                    .changeAddress(any(), any(), any(), any());

            // when
            final ChangeAddressRequest request = new ChangeAddressRequest(12345, "기본 주소", "상세 주소");
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/ChangeAddress",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("postcode").description("변경할 우편번호"),
                                            fieldWithPath("defaultAddress").description("변경할 기본 주소"),
                                            fieldWithPath("detailAddress").description("변경할 상세 주소")
                                    )
                            )
                    );
        }
    }
}

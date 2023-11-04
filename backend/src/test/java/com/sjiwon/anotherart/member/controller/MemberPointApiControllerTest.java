package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.controller.dto.request.PointChargeRequest;
import com.sjiwon.anotherart.member.controller.dto.request.PointRefundRequest;
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

@DisplayName("Member [Controller Layer] -> MemberPointApiController 테스트")
class MemberPointApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("포인트 충전 API [POST /api/members/{memberId}/point/charge] - AccessToken 필수")
    class changeNickname {
        private static final String BASE_URL = "/api/members/{memberId}/point/charge";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("포인트 충전을 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(memberPointService)
                    .chargePoint(any(), any());

            // when
            final PointChargeRequest request = new PointChargeRequest(50_000);
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Point/Charge",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("chargeAmount").description("충전할 포인트 금액")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("포인트 환불 API [POST /api/members/{memberId}/point/refund] - AccessToken 필수")
    class refundPoint {
        private static final String BASE_URL = "/api/members/{memberId}/point/refund";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("보유하고 있는 포인트가 부족함에 따라 포인트 환불을 실패한다")
        void throwExceptionByPointIsNotEnough() throws Exception {
            // given
            doThrow(AnotherArtException.type(MemberErrorCode.POINT_IS_NOT_ENOUGH))
                    .when(memberPointService)
                    .refundPoint(any(), any());

            // when
            final PointRefundRequest request = new PointRefundRequest(50_000);
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.POINT_IS_NOT_ENOUGH;
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
                                    "MemberApi/Point/Refund/Failure",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("refundAmount").description("환불할 포인트 금액")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @WithMockUser
        @DisplayName("포인트 환불을 성공한다")
        void success() throws Exception {
            // given
            doNothing()
                    .when(memberPointService)
                    .refundPoint(any(), any());

            // when
            final PointRefundRequest request = new PointRefundRequest(50_000);
            final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "MemberApi/Point/Refund/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("refundAmount").description("환불할 포인트 금액")
                                    )
                            )
                    );
        }
    }
}

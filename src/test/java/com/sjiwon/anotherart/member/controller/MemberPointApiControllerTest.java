package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberPointApiController 테스트")
class MemberPointApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 포인트 충전 테스트 [POST /api/member/point/charge]")
    class chargePoint {
        private static final String BASE_URL = "/api/member/point/charge";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Member member = signUpMember();
            final int initAmount = member.getAvailablePoint();
            final int chargeAmount = 15000;
            
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .param("chargeAmount", String.valueOf(chargeAmount));
            
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
                                    "MemberApi/ChargePoint/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("chargeAmount").description("충전할 포인트 금액")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );

            // 포인트 내역 -> 충전 실패에 따라 회원가입 내역만 존재
            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(1);
            assertThat(pointDetails.get(0).getMember().getId()).isEqualTo(member.getId());
            assertThat(pointDetails.get(0).getPointType()).isEqualTo(PointType.JOIN);
            assertThat(pointDetails.get(0).getAmount()).isEqualTo(0);

            assertThat(member.getAvailablePoint()).isEqualTo(initAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount);
        }
        
        @Test
        @DisplayName("포인트 충전에 성공한다")
        void test2() throws Exception {
            // given
            Member member = signUpMember();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final int initAmount = member.getAvailablePoint();
            final int chargeAmount = 15000;

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("chargeAmount", String.valueOf(chargeAmount));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/ChargePoint/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("chargeAmount").description("충전할 포인트 금액")
                                    )
                            )
                    );

            // 포인트 내역 -> 충전 성공에 따라 회원가입 + 충전 내역 존재
            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(2);

            PointDetail joinDetail = pointDetails.get(0);
            assertThat(joinDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(joinDetail.getPointType()).isEqualTo(PointType.JOIN);
            assertThat(joinDetail.getAmount()).isEqualTo(0);

            PointDetail chargeDetail = pointDetails.get(1);
            assertThat(chargeDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(chargeDetail.getPointType()).isEqualTo(PointType.CHARGE);
            assertThat(chargeDetail.getAmount()).isEqualTo(chargeAmount);

            assertThat(member.getAvailablePoint()).isEqualTo(initAmount + chargeAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount + chargeAmount);
        }
    }

    @Nested
    @DisplayName("사용자 포인트 환불 테스트 [POST /api/member/point/refund]")
    class refundPoint {
        private static final String BASE_URL = "/api/member/point/refund";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            final int chargeAmount = 10000;
            Member member = signUpAndChargePoint(chargeAmount);
            final int initAmount = member.getAvailablePoint();
            final int refundAmount = 15000;

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .param("refundAmount", String.valueOf(refundAmount));

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
                                    "MemberApi/RefundPoint/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("refundAmount").description("환불할 포인트 금액")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
            
            // 포인트 내역 -> 환불 실패에 따라 회원가입 + 충전 내역만 존재
            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(2);

            PointDetail joinDetail = pointDetails.get(0);
            assertThat(joinDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(joinDetail.getPointType()).isEqualTo(PointType.JOIN);
            assertThat(joinDetail.getAmount()).isEqualTo(0);

            PointDetail chargeDetail = pointDetails.get(1);
            assertThat(chargeDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(chargeDetail.getPointType()).isEqualTo(PointType.CHARGE);
            assertThat(chargeDetail.getAmount()).isEqualTo(chargeAmount);

            assertThat(member.getAvailablePoint()).isEqualTo(initAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount);
        }
        
        @Test
        @DisplayName("보유한 포인트가 부족함에 따라 포인트 환불이 불가능하다")
        void test2() throws Exception {
            // given
            final int chargeAmount = 10000;
            Member member = signUpAndChargePoint(chargeAmount);
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final int initAmount = member.getAvailablePoint();
            final int refundAmount = 15000;

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("refundAmount", String.valueOf(refundAmount));

            // then
            final MemberErrorCode expectedError = MemberErrorCode.INVALID_POINT_DECREASE;
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
                                    "MemberApi/RefundPoint/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("refundAmount").description("환불할 포인트 금액")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );

            // 포인트 내역 -> 환불 실패에 따라 회원가입 + 충전 내역만 존재
            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(2);

            PointDetail joinDetail = pointDetails.get(0);
            assertThat(joinDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(joinDetail.getPointType()).isEqualTo(PointType.JOIN);
            assertThat(joinDetail.getAmount()).isEqualTo(0);

            PointDetail chargeDetail = pointDetails.get(1);
            assertThat(chargeDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(chargeDetail.getPointType()).isEqualTo(PointType.CHARGE);
            assertThat(chargeDetail.getAmount()).isEqualTo(chargeAmount);

            assertThat(member.getAvailablePoint()).isEqualTo(initAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount);
        }
        
        @Test
        @DisplayName("포인트 환불에 성공한다")
        void test3() throws Exception {
            // given
            final int chargeAmount = 20000;
            Member member = signUpAndChargePoint(chargeAmount);
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final int initAmount = member.getAvailablePoint();
            final int refundAmount = 15000;

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("refundAmount", String.valueOf(refundAmount));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "MemberApi/RefundPoint/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    requestParameters(
                                            parameterWithName("refundAmount").description("환불할 포인트 금액")
                                    )
                            )
                    );

            // 포인트 내역 -> 환불 성공에 따라 회원가입 + 충전 + 환불 내역 존재
            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(3);

            PointDetail joinDetail = pointDetails.get(0);
            assertThat(joinDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(joinDetail.getPointType()).isEqualTo(PointType.JOIN);
            assertThat(joinDetail.getAmount()).isEqualTo(0);

            PointDetail chargeDetail = pointDetails.get(1);
            assertThat(chargeDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(chargeDetail.getPointType()).isEqualTo(PointType.CHARGE);
            assertThat(chargeDetail.getAmount()).isEqualTo(chargeAmount);

            PointDetail refundDetail = pointDetails.get(2);
            assertThat(refundDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(refundDetail.getPointType()).isEqualTo(PointType.REFUND);
            assertThat(refundDetail.getAmount()).isEqualTo(refundAmount);

            assertThat(member.getAvailablePoint()).isEqualTo(initAmount - refundAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount - refundAmount);
        }
    }

    private Member signUpMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member signUpAndChargePoint(int chargeAmount) {
        Member member = MemberFixture.A.toMember();
        member.addPointDetail(PointDetail.insertPointDetail(member, PointType.CHARGE, chargeAmount));
        return memberRepository.save(member);
    }
}

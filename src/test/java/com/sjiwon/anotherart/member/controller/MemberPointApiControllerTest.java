package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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
@RequiredArgsConstructor
class MemberPointApiControllerTest extends ControllerTest {
    private final MockMvc mockMvc;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PointDetailRepository pointDetailRepository;

    private static final PasswordEncoder ENCODER = PasswordEncoderUtils.getEncoder();
    private static final String BEARER_TOKEN = "Bearer ";

    @Nested
    @DisplayName("사용자 포인트 충전 테스트 [PATCH /api/member/point/charge]")
    class chargePoint {
        private static final String BASE_URL = "/api/member/point/charge";

        @Test
        @DisplayName("Access Token이 없음에 따라 포인트 충전이 불가능하다")
        void test1() throws Exception {
            // given
            Member member = signUpMember();
            final int initAmount = member.getAvailablePoint().getValue();
            final int chargeAmount = 15000;
            
            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .param("chargeAmount", String.valueOf(chargeAmount))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);
            
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
            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(1);
            assertThat(pointDetails.get(0).getMember().getId()).isEqualTo(member.getId());
            assertThat(pointDetails.get(0).getPointType()).isEqualTo(PointType.JOIN);
            assertThat(pointDetails.get(0).getAmount()).isEqualTo(0);

            assertThat(member.getAvailablePoint().getValue()).isEqualTo(initAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount);
        }
        
        @Test
        @DisplayName("포인트 충전에 성공한다")
        void test2() throws Exception {
            // given
            Member member = signUpMember();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());
            final int initAmount = member.getAvailablePoint().getValue();
            final int chargeAmount = 15000;

            // when
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("chargeAmount", String.valueOf(chargeAmount))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED);

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

            List<PointDetail> pointDetails = pointDetailRepository.findAll();
            assertThat(pointDetails.size()).isEqualTo(2);

            PointDetail joinDetail = pointDetails.get(0);
            PointDetail chargeDetail = pointDetails.get(1);
            assertThat(joinDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(joinDetail.getPointType()).isEqualTo(PointType.JOIN);
            assertThat(joinDetail.getAmount()).isEqualTo(0);
            assertThat(chargeDetail.getMember().getId()).isEqualTo(member.getId());
            assertThat(chargeDetail.getPointType()).isEqualTo(PointType.CHARGE);
            assertThat(chargeDetail.getAmount()).isEqualTo(chargeAmount);

            assertThat(member.getAvailablePoint().getValue()).isEqualTo(initAmount + chargeAmount);
            assertThat(member.getTotalPoints()).isEqualTo(initAmount + chargeAmount);
        }
    }

    private Member signUpMember() {
        Member member = memberRepository.save(MemberFixture.A.toMember(ENCODER));
        pointDetailRepository.save(PointDetail.createPointDetail(member));
        return member;
    }
}

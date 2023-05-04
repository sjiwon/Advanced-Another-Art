package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberInformationApiController 테스트")
class MemberInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 기본 정보 조회 API [GET /api/members/{memberId}]")
    class getInformation {
        private static final String BASE_URL = "/api/members/{memberId}";
        private static final Long MEMBER_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 기본 정보 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/Information/Basic/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("Token Payload가 Endpoint의 memberId와 일치하지 않음에 따라 기본 정보 조회에 실패한다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/Information/Basic/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용자 기본 정보를 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            MemberInformation response = generateMemberInformationResponse();
            given(memberInformationService.getInformation(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/Basic/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID(PK)"),
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("nickname").description("사용자 닉네임"),
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("school").description("사용자 재학중인 학교"),
                                            fieldWithPath("phone").description("사용자 전화번호"),
                                            fieldWithPath("email").description("사용자 이메일"),
                                            fieldWithPath("address.postcode").description("우편번호"),
                                            fieldWithPath("address.defaultAddress").description("기본 주소"),
                                            fieldWithPath("address.detailAddress").description("상세 주소"),
                                            fieldWithPath("totalPoint").description("전체 포인트"),
                                            fieldWithPath("availablePoint").description("사용 가능한 포인트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자 포인트 활용 내역 조회 API [GET /api/members/{memberId}/points]")
    class getPointRecords {
        private static final String BASE_URL = "/api/members/{memberId}/points";
        private static final Long MEMBER_ID = 1L;
        private static final Long ANONYMOUS_ID = 2L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 포인트 활용 내역 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID);

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/Information/PointRecord/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("Token Payload가 Endpoint의 memberId와 일치하지 않음에 따라 포인트 활용 내역 조회에 실패한다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(ANONYMOUS_ID);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            final AuthErrorCode expectedError = AuthErrorCode.INVALID_PERMISSION;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "MemberApi/Information/PointRecord/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("포인트 활용 내역을 조회한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);

            PointRecordAssembler response = generatePointRecords();
            given(memberInformationService.getPointRecords(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/PointRecord/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].pointType").description("활용 타입"),
                                            fieldWithPath("result[].amount").description("활용 금액"),
                                            fieldWithPath("result[].recordDate").description("활용 날짜")
                                    )
                            )
                    );
        }
    }

    private MemberInformation generateMemberInformationResponse() {
        Member member = createMember(MEMBER_A, 1L);
        return new MemberInformation(member);
    }

    private Member createMember(MemberFixture fixture, Long id) {
        Member member = fixture.toMember();
        ReflectionTestUtils.setField(member, "id", id);

        member.addPointRecords(CHARGE, 100_000);
        member.decreaseAvailablePoint(12_000);

        return member;
    }

    private PointRecordAssembler generatePointRecords() {
        List<MemberPointRecord> result = List.of(
                new MemberPointRecord(PURCHASE, 100_000, LocalDateTime.now().minusDays(1)),
                new MemberPointRecord(PURCHASE, 50_000, LocalDateTime.now().minusDays(2)),
                new MemberPointRecord(SOLD, 35_000, LocalDateTime.now().minusDays(3)),
                new MemberPointRecord(REFUND, 50_000, LocalDateTime.now().minusDays(4)),
                new MemberPointRecord(PURCHASE, 385_000, LocalDateTime.now().minusDays(5)),
                new MemberPointRecord(CHARGE, 1_000_000, LocalDateTime.now().minusDays(6))
        );

        return new PointRecordAssembler(result);
    }
}

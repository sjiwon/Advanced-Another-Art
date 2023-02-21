package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ArtUtils;
import com.sjiwon.anotherart.common.utils.BasicArtBuilder;
import com.sjiwon.anotherart.common.utils.SimpleArtBuilder;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.infra.query.dto.response.UserPointHistory;
import com.sjiwon.anotherart.member.service.dto.response.UserProfile;
import com.sjiwon.anotherart.member.service.dto.response.UserTradedArt;
import com.sjiwon.anotherart.member.service.dto.response.UserWinningAuction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
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
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

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
        @DisplayName("사용자의 프로필 정보 조회에 성공한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(memberId, ROLE_USER);

            setMemberId();
            UserProfile response = UserProfile.builder()
                    .member(DEFAULT_MEMBER)
                    .totalPoint(DEFAULT_TOTAL_POINT)
                    .build();
            given(memberProfileService.getUserProfile(memberId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

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

    @Nested
    @DisplayName("사용자 포인트 내역 조회 테스트 [GET /api/members/{memberId}/points]")
    class userPointHistory {
        private static final String BASE_URL = "/api/members/{memberId}/points";

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
                                    "MemberProfileApi/UserPointHistory/Failure/Case1",
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
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

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
                                    "MemberProfileApi/UserPointHistory/Failure/Case2",
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
        @DisplayName("사용자의 포인트 내역 조회에 성공한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(payloadId, ROLE_USER);

            List<UserPointHistory> response = List.of(
                    UserPointHistory.builder()
                            .pointType(PointType.CHARGE)
                            .dealAmount(100_000_000)
                            .recordDate(LocalDateTime.now().minusDays(5))
                            .build(),
                    UserPointHistory.builder()
                            .pointType(PointType.REFUND)
                            .dealAmount(100_000_000)
                            .recordDate(LocalDateTime.now().minusDays(3))
                            .build(),
                    UserPointHistory.builder()
                            .pointType(PointType.CHARGE)
                            .dealAmount(100_000_000)
                            .recordDate(LocalDateTime.now().minusDays(1))
                            .build()
            );
            given(memberProfileService.getUserPointHistory(memberId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.size()").value(response.size()))
                    .andDo(
                            document(
                                    "MemberProfileApi/UserPointHistory/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].pointType").description("포인트 거래 유형 [충전/환불/작품 구매/작품 판매]"),
                                            fieldWithPath("result[].dealAmount").description("포인트 거래량"),
                                            fieldWithPath("result[].recordDate").description("포인트 거래 날짜")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 낙찰된 경매 작품 조회 테스트 [GET /api/members/{memberId}/winning-auctions]")
    class userWinningAuction {
        private static final String BASE_URL = "/api/members/{memberId}/winning-auctions";

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
                                    "MemberProfileApi/UserWinningAuction/Failure/Case1",
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
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

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
                                    "MemberProfileApi/UserWinningAuction/Failure/Case2",
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
        @DisplayName("사용자의 낙찰된 경매 작품 조회에 성공한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(payloadId, ROLE_USER);

            List<UserWinningAuction> response = List.of(
                    UserWinningAuction.builder()
                            .art(BasicArtBuilder.createBasicAuctionArt(ArtFixture.A))
                            .hashtags(ArtUtils.HASHTAGS)
                            .build(),
                    UserWinningAuction.builder()
                            .art(BasicArtBuilder.createBasicAuctionArt(ArtFixture.C))
                            .hashtags(ArtUtils.UPDATE_HASHTAGS)
                            .build()
            );
            given(memberProfileWithArtService.getWinningAuction(memberId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.size()").value(response.size()))
                    .andDo(
                            document(
                                    "MemberProfileApi/UserWinningAuction/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("result[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("result[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("result[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("result[].art.highestBidderId").description("최고 입찰자 ID(PK)"),
                                            fieldWithPath("result[].art.highestBidderNickname").description("최고 입찰자 닉네임"),
                                            fieldWithPath("result[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교"),
                                            fieldWithPath("result[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("경매 작품명"),
                                            fieldWithPath("result[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("result[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("result[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("result[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("result[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("result[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("result[].hashtags").description("작품의 해시태그 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 판매한 경매 작품 조회 테스트 [GET /api/members/{memberId}/auctions/sold]")
    class userSoldAuctionArt {
        private static final String BASE_URL = "/api/members/{memberId}/auctions/sold";

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
                                    "MemberProfileApi/UserSoldAuctionArt/Failure/Case1",
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
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

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
                                    "MemberProfileApi/UserSoldAuctionArt/Failure/Case2",
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
        @DisplayName("사용자가 판매한 경매 작품 조회에 성공한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(payloadId, ROLE_USER);

            List<UserTradedArt> response = List.of(
                    UserTradedArt.builder()
                            .art(SimpleArtBuilder.createSimpleTradedArt(ArtFixture.A))
                            .hashtags(ArtUtils.HASHTAGS)
                            .build(),
                    UserTradedArt.builder()
                            .art(SimpleArtBuilder.createSimpleTradedArt(ArtFixture.B))
                            .hashtags(ArtUtils.HASHTAGS)
                            .build(),
                    UserTradedArt.builder()
                            .art(SimpleArtBuilder.createSimpleTradedArt(ArtFixture.C))
                            .hashtags(ArtUtils.UPDATE_HASHTAGS)
                            .build()
            );
            given(memberProfileWithArtService.getSoldAuctionArt(memberId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.size()").value(response.size()))
                    .andDo(
                            document(
                                    "MemberProfileApi/UserSoldAuctionArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("result[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("result[].art.buyerId").description("작품 구매자 ID(PK)"),
                                            fieldWithPath("result[].art.buyerNickname").description("작품 구매자 닉네임"),
                                            fieldWithPath("result[].art.buyerSchool").description("작품 구매자 재학중인 학교"),
                                            fieldWithPath("result[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("경매 작품명"),
                                            fieldWithPath("result[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("result[].art.purchasePrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("result[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("result[].hashtags").description("작품의 해시태그 리스트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 판매한 일반 작품 조회 테스트 [GET /api/members/{memberId}/generals/sold]")
    class userSoldGeneralArt {
        private static final String BASE_URL = "/api/members/{memberId}/generals/sold";

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
                                    "MemberProfileApi/UserSoldGeneralArt/Failure/Case1",
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
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

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
                                    "MemberProfileApi/UserSoldGeneralArt/Failure/Case2",
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
        @DisplayName("사용자가 판매한 일반 작품 조회에 성공한다")
        void test3() throws Exception {
            // given
            Long memberId = 1L;
            Long payloadId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(payloadId, ROLE_USER);

            List<UserTradedArt> response = List.of(
                    UserTradedArt.builder()
                            .art(SimpleArtBuilder.createSimpleTradedArt(ArtFixture.A))
                            .hashtags(ArtUtils.HASHTAGS)
                            .build(),
                    UserTradedArt.builder()
                            .art(SimpleArtBuilder.createSimpleTradedArt(ArtFixture.B))
                            .hashtags(ArtUtils.HASHTAGS)
                            .build(),
                    UserTradedArt.builder()
                            .art(SimpleArtBuilder.createSimpleTradedArt(ArtFixture.C))
                            .hashtags(ArtUtils.UPDATE_HASHTAGS)
                            .build()
            );
            given(memberProfileWithArtService.getSoldGeneralArt(memberId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, memberId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").exists())
                    .andExpect(jsonPath("$.result.size()").value(response.size()))
                    .andDo(
                            document(
                                    "MemberProfileApi/UserSoldGeneralArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("result[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("result[].art.buyerId").description("작품 구매자 ID(PK)"),
                                            fieldWithPath("result[].art.buyerNickname").description("작품 구매자 닉네임"),
                                            fieldWithPath("result[].art.buyerSchool").description("작품 구매자 재학중인 학교"),
                                            fieldWithPath("result[].art.artId").description("일반 작품 ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("일반 작품명"),
                                            fieldWithPath("result[].art.artDescription").description("일반 작품 설명"),
                                            fieldWithPath("result[].art.purchasePrice").description("일반 작품 가격"),
                                            fieldWithPath("result[].art.artStorageName").description("일반 작품 서버 저장명(UUID)"),
                                            fieldWithPath("result[].hashtags").description("작품의 해시태그 리스트")
                                    )
                            )
                    );
        }
    }

    private void setMemberId() {
        ReflectionTestUtils.setField(DEFAULT_MEMBER, "id", 1L);
    }
}
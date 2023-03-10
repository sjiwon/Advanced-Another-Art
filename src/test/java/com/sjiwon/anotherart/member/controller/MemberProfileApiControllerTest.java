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

@DisplayName("Member [Controller Layer] -> MemberProfileApiController ?????????")
class MemberProfileApiControllerTest extends ControllerTest {
    private static final Member DEFAULT_MEMBER = MemberFixture.A.toMember();
    private static final int DEFAULT_TOTAL_POINT = 100_000_000;

    @Nested
    @DisplayName("????????? ????????? ?????? ?????? ????????? [GET /api/members/{memberId}]")
    class userProfile {
        private static final String BASE_URL = "/api/members/{memberId}";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("????????? ID(PK)"),
                                            fieldWithPath("name").description("??????"),
                                            fieldWithPath("nickname").description("?????????"),
                                            fieldWithPath("loginId").description("????????? ?????????"),
                                            fieldWithPath("email").description("?????????"),
                                            fieldWithPath("school").description("???????????? ?????????"),
                                            fieldWithPath("phone").description("????????????"),
                                            fieldWithPath("address.postcode").description("????????????"),
                                            fieldWithPath("address.defaultAddress").description("?????? ??????"),
                                            fieldWithPath("address.detailAddress").description("?????? ??????"),
                                            fieldWithPath("availablePoint").description("?????? ????????? ?????????"),
                                            fieldWithPath("totalPoint").description("?????? ?????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("????????? ????????? ?????? ?????? ????????? [GET /api/members/{memberId}/points]")
    class userPointHistory {
        private static final String BASE_URL = "/api/members/{memberId}/points";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].pointType").description("????????? ?????? ?????? [??????/??????/?????? ??????/?????? ??????]"),
                                            fieldWithPath("result[].dealAmount").description("????????? ?????????"),
                                            fieldWithPath("result[].recordDate").description("????????? ?????? ??????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ?????? ?????? ?????? ????????? [GET /api/members/{memberId}/winning-auctions]")
    class userWinningAuction {
        private static final String BASE_URL = "/api/members/{memberId}/winning-auctions";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.auctionId").description("?????? ID(PK)"),
                                            fieldWithPath("result[].art.highestBidPrice").description("?????? ?????? ?????????"),
                                            fieldWithPath("result[].art.auctionStartDate").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.auctionEndDate").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.highestBidderId").description("?????? ????????? ID(PK)"),
                                            fieldWithPath("result[].art.highestBidderNickname").description("?????? ????????? ?????????"),
                                            fieldWithPath("result[].art.highestBidderSchool").description("?????? ????????? ???????????? ??????"),
                                            fieldWithPath("result[].art.artId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("?????? ?????????"),
                                            fieldWithPath("result[].art.artDescription").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artPrice").description("?????? ?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artStatus").description("?????? ?????? ?????? (?????? ??? / ?????? ??????)"),
                                            fieldWithPath("result[].art.artRegistrationDate").description("?????? ?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artStorageName").description("?????? ?????? ?????? ?????????(UUID)"),
                                            fieldWithPath("result[].art.ownerId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("?????? ?????? ?????????"),
                                            fieldWithPath("result[].art.ownerSchool").description("?????? ?????? ???????????? ??????"),
                                            fieldWithPath("result[].hashtags").description("????????? ???????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ?????? ?????? ?????? ????????? [GET /api/members/{memberId}/auctions/sold]")
    class userSoldAuctionArt {
        private static final String BASE_URL = "/api/members/{memberId}/auctions/sold";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.ownerId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("?????? ?????? ?????????"),
                                            fieldWithPath("result[].art.ownerSchool").description("?????? ?????? ???????????? ??????"),
                                            fieldWithPath("result[].art.buyerId").description("?????? ????????? ID(PK)"),
                                            fieldWithPath("result[].art.buyerNickname").description("?????? ????????? ?????????"),
                                            fieldWithPath("result[].art.buyerSchool").description("?????? ????????? ???????????? ??????"),
                                            fieldWithPath("result[].art.artId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("?????? ?????????"),
                                            fieldWithPath("result[].art.artDescription").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.purchasePrice").description("?????? ?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artStorageName").description("?????? ?????? ?????? ?????????(UUID)"),
                                            fieldWithPath("result[].hashtags").description("????????? ???????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ?????? ?????? ?????? ????????? [GET /api/members/{memberId}/generals/sold]")
    class userSoldGeneralArt {
        private static final String BASE_URL = "/api/members/{memberId}/generals/sold";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ?????? ????????? ????????????")
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
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.ownerId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("?????? ?????? ?????????"),
                                            fieldWithPath("result[].art.ownerSchool").description("?????? ?????? ???????????? ??????"),
                                            fieldWithPath("result[].art.buyerId").description("?????? ????????? ID(PK)"),
                                            fieldWithPath("result[].art.buyerNickname").description("?????? ????????? ?????????"),
                                            fieldWithPath("result[].art.buyerSchool").description("?????? ????????? ???????????? ??????"),
                                            fieldWithPath("result[].art.artId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("?????? ?????????"),
                                            fieldWithPath("result[].art.artDescription").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.purchasePrice").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artStorageName").description("?????? ?????? ?????? ?????????(UUID)"),
                                            fieldWithPath("result[].hashtags").description("????????? ???????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ?????? ?????? ?????? ????????? [GET /api/members/{memberId}/auctions/purchase]")
    class userPurchaseAuctionArt {
        private static final String BASE_URL = "/api/members/{memberId}/auctions/purchase";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                    "MemberProfileApi/UserPurchaseAuctionArt/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                    "MemberProfileApi/UserPurchaseAuctionArt/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ?????? ????????? ????????????")
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
            given(memberProfileWithArtService.getPurchaseAuctionArt(memberId)).willReturn(response);

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
                                    "MemberProfileApi/UserPurchaseAuctionArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.ownerId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("?????? ?????? ?????????"),
                                            fieldWithPath("result[].art.ownerSchool").description("?????? ?????? ???????????? ??????"),
                                            fieldWithPath("result[].art.buyerId").description("?????? ????????? ID(PK)"),
                                            fieldWithPath("result[].art.buyerNickname").description("?????? ????????? ?????????"),
                                            fieldWithPath("result[].art.buyerSchool").description("?????? ????????? ???????????? ??????"),
                                            fieldWithPath("result[].art.artId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("?????? ?????????"),
                                            fieldWithPath("result[].art.artDescription").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.purchasePrice").description("?????? ?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artStorageName").description("?????? ?????? ?????? ?????????(UUID)"),
                                            fieldWithPath("result[].hashtags").description("????????? ???????????? ?????????")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("???????????? ????????? ?????? ?????? ?????? ????????? [GET /api/members/{memberId}/generals/purchase]")
    class userPurchaseGeneralArt {
        private static final String BASE_URL = "/api/members/{memberId}/generals/purchase";

        @Test
        @DisplayName("Authorization ????????? Access Token??? ????????? ?????? ????????? ????????????")
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
                                    "MemberProfileApi/UserPurchaseGeneralArt/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("Access Token Payload??? memberId??? PathVariable??? memberId??? ???????????? ????????? ?????? ????????? ????????????")
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
                                    "MemberProfileApi/UserPurchaseGeneralArt/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("????????? ID(PK)")
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
        @DisplayName("???????????? ????????? ?????? ?????? ????????? ????????????")
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
            given(memberProfileWithArtService.getPurchaseGeneralArt(memberId)).willReturn(response);

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
                                    "MemberProfileApi/UserPurchaseGeneralArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("memberId").description("????????? ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.ownerId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.ownerNickname").description("?????? ?????? ?????????"),
                                            fieldWithPath("result[].art.ownerSchool").description("?????? ?????? ???????????? ??????"),
                                            fieldWithPath("result[].art.buyerId").description("?????? ????????? ID(PK)"),
                                            fieldWithPath("result[].art.buyerNickname").description("?????? ????????? ?????????"),
                                            fieldWithPath("result[].art.buyerSchool").description("?????? ????????? ???????????? ??????"),
                                            fieldWithPath("result[].art.artId").description("?????? ?????? ID(PK)"),
                                            fieldWithPath("result[].art.artName").description("?????? ?????????"),
                                            fieldWithPath("result[].art.artDescription").description("?????? ?????? ??????"),
                                            fieldWithPath("result[].art.purchasePrice").description("?????? ?????? ?????? ??????"),
                                            fieldWithPath("result[].art.artStorageName").description("?????? ?????? ?????? ?????????(UUID)"),
                                            fieldWithPath("result[].hashtags").description("????????? ???????????? ?????????")
                                    )
                            )
                    );
        }
    }

    private void setMemberId() {
        ReflectionTestUtils.setField(DEFAULT_MEMBER, "id", 1L);
    }
}
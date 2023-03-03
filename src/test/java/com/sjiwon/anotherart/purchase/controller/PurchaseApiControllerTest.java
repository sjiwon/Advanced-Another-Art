package com.sjiwon.anotherart.purchase.controller;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ArtUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.AuctionFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.ROLE_USER;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

@DisplayName("Purchase [Controller Layer] -> PurchaseApiController 테스트")
class PurchaseApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("작품 구매 테스트 [POST /api/art/{artId}/purchase]")
    class bid {
        private static final String BASE_URL = "/api/art/{artId}/purchase";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId);

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
                                    "PurchaseApi/PurchaseAuctionArt/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
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
        @DisplayName("작품 소유자는 본인의 작품을 구매할 수 없고 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long ownerId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(ownerId, ROLE_USER);

            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            doThrow(AnotherArtException.type(PurchaseErrorCode.INVALID_OWNER_PURCHASE))
                    .when(purchaseService)
                    .purchaseArt(artId, ownerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.INVALID_OWNER_PURCHASE;
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
                                    "PurchaseApi/PurchaseAuctionArt/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
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
        @DisplayName("경매가 진행중인 작품은 구매할 수 없고 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doThrow(AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED))
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.AUCTION_NOT_FINISHED;
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
                                    "PurchaseApi/PurchaseAuctionArt/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
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
        @DisplayName("종료된 경매에 대해서 최고 입찰자가 아닌 사용자가 구매 요청을 하면 예외가 발생한다")
        void test4() throws Exception {
            // given
            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime3DayAgo, currentTime1DayAgo);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doThrow(AnotherArtException.type(PurchaseErrorCode.INVALID_HIGHEST_BIDDER))
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.INVALID_HIGHEST_BIDDER;
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
                                    "PurchaseApi/PurchaseAuctionArt/Failure/Case4",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
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
        @DisplayName("이미 판매 완료된 경매 작품에 대한 고의적인 API 호출로 구매 요청을 진행할 경우 예외가 발생한다")
        void test5() throws Exception {
            // given
            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime3DayAgo, currentTime1DayAgo);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doThrow(AnotherArtException.type(PurchaseErrorCode.ART_ALREADY_SOLD_OUT))
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.ART_ALREADY_SOLD_OUT;
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
                                    "PurchaseApi/PurchaseAuctionArt/Failure/Case5",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
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
        @DisplayName("입찰된 경매 작품에 대해서 구매 확정을 진행할 때 사용 가능한 포인트가 부족함에 따라 예외가 발생한다")
        void test6() throws Exception {
            // given
            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime3DayAgo, currentTime1DayAgo);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doThrow(AnotherArtException.type(PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT))
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT;
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
                                    "PurchaseApi/PurchaseAuctionArt/Failure/Case6",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
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
        @DisplayName("경매 작품 구매에 성공한다")
        void test7() throws Exception {
            // given
            Long artId = 1L;
            Art auctionArt = createMockAuctionArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(auctionArt);

            Auction auction = createMockAuction(auctionArt, currentTime3DayAgo, currentTime1DayAgo);
            given(auctionFindService.findByArtId(artId)).willReturn(auction);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doNothing()
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "PurchaseApi/PurchaseAuctionArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 경매 작품 ID(PK)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("이미 판매 완료된 일반 작품에 대한 고의적인 API 호출로 구매 요청을 진행할 경우 예외가 발생한다")
        void test8() throws Exception {
            // given
            Long artId = 1L;
            Art generalArt = createMockGeneralArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(generalArt);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doThrow(AnotherArtException.type(PurchaseErrorCode.ART_ALREADY_SOLD_OUT))
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.ART_ALREADY_SOLD_OUT;
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
                                    "PurchaseApi/PurchaseGeneralArt/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 일반 작품 ID(PK)")
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
        @DisplayName("일반 작품을 구매할 때 사용 가능한 포인트가 부족함에 따라 예외가 발생한다")
        void test9() throws Exception {
            // given
            Long artId = 1L;
            Art generalArt = createMockGeneralArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(generalArt);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doThrow(AnotherArtException.type(PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT))
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT;
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
                                    "PurchaseApi/PurchaseGeneralArt/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 일반 작품 ID(PK)")
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
        @DisplayName("일반 작품 구매에 성공한다")
        void test10() throws Exception {
            Long artId = 1L;
            Art generalArt = createMockGeneralArt(ArtUtils.HASHTAGS);
            given(artFindService.findById(artId)).willReturn(generalArt);

            Long buyerId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(buyerId, ROLE_USER);

            doNothing()
                    .when(purchaseService)
                    .purchaseArt(artId, buyerId);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, artId)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$.statusCode").doesNotExist())
                    .andDo(
                            document(
                                    "PurchaseApi/PurchaseGeneralArt/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("artId").description("구매할 일반 작품 ID(PK)")
                                    )
                            )
                    );
        }
    }

    private Art createMockAuctionArt(List<String> hashtags) {
        return ArtFixture.A.toArt(MemberFixture.A.toMember(), hashtags);
    }

    private Art createMockGeneralArt(List<String> hashtags) {
        return ArtFixture.B.toArt(MemberFixture.A.toMember(), hashtags);
    }

    private Auction createMockAuction(Art art, LocalDateTime startDate, LocalDateTime endDate) {
        return AuctionFixture.toAuction(art, startDate, endDate);
    }
}
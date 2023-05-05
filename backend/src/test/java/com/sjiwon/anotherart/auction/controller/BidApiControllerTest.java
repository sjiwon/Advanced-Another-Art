package com.sjiwon.anotherart.auction.controller;

import com.sjiwon.anotherart.auction.controller.dto.request.BidRequest;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.exception.MemberErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
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

@DisplayName("Auction [Controller Layer] -> BidApiController 테스트")
class BidApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("경매 작품 입찰 API [POST /api/auctions/{auctionId}/bid]")
    class bid {
        private static final String BASE_URL = "/api/auctions/{auctionId}/bid";
        private static final Long AUCTION_ID = 1L;
        private static final Long MEMBER_ID = 1L;

        @Test
        @DisplayName("Authorization Header에 AccessToken이 없으면 기본 정보 조회에 실패한다")
        void withoutAccessToken() throws Exception {
            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

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
                                    "AuctionApi/Bid/Failure/Case1",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("경매가 진행중이지 않으면 입찰을 할 수 없다")
        void throwExceptionByAuctionIsNotInProgess() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doThrow(AnotherArtException.type(AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS))
                    .when(bidService)
                    .bid(any(), any(), any());

            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.AUCTION_IS_NOT_IN_PROGRESS;
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
                                    "AuctionApi/Bid/Failure/Case2",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("경매 작품 소유자는 본인 작품에 입찰을 할 수 없다")
        void throwExceptionByArtOwnerCannotBid() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doThrow(AnotherArtException.type(AuctionErrorCode.ART_OWNER_CANNOT_BID))
                    .when(bidService)
                    .bid(any(), any(), any());

            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.ART_OWNER_CANNOT_BID;
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
                                    "AuctionApi/Bid/Failure/Case3",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("현재 최고 입찰자는 연속으로 다시 입찰을 진행할 수 없다")
        void throwExceptionByHighestBidderCannotBidAgain() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doThrow(AnotherArtException.type(AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN))
                    .when(bidService)
                    .bid(any(), any(), any());

            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.HIGHEST_BIDDER_CANNOT_BID_AGAIN;
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
                                    "AuctionApi/Bid/Failure/Case4",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("입찰가가 부족하면 입찰을 진행할 수 없다")
        void throwExceptionByBidPriceIsNotEnough() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doThrow(AnotherArtException.type(AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH))
                    .when(bidService)
                    .bid(any(), any(), any());

            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.BID_PRICE_IS_NOT_ENOUGH;
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.status").exists(),
                            jsonPath("$.status").value(expectedError.getStatus().value()),
                            jsonPath("$.errorCode").exists(),
                            jsonPath("$.errorCode").value(expectedError.getErrorCode()),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(expectedError.getMessage())
                    )
                    .andDo(
                            document(
                                    "AuctionApi/Bid/Failure/Case5",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("사용 가능한 포인트가 부족하면 입찰을 진행할 수 없다")
        void throwExceptionByPointIsNotEnough() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doThrow(AnotherArtException.type(MemberErrorCode.POINT_IS_NOT_ENOUGH))
                    .when(bidService)
                    .bid(any(), any(), any());

            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
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
                                    "AuctionApi/Bid/Failure/Case6",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    ),
                                    getExceptionResponseFiels()
                            )
                    );
        }

        @Test
        @DisplayName("입찰을 성공한다")
        void success() throws Exception {
            // given
            given(jwtTokenProvider.isTokenValid(anyString())).willReturn(true);
            given(jwtTokenProvider.getId(anyString())).willReturn(MEMBER_ID);
            doNothing()
                    .when(bidService)
                    .bid(any(), any(), any());

            // when
            final BidRequest request = new BidRequest(100_000);
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, AUCTION_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN))
                    .contentType(APPLICATION_JSON)
                    .content(convertObjectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "AuctionApi/Bid/Success",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidPrice").description("입찰 가격")
                                    )
                            )
                    );
        }
    }
}

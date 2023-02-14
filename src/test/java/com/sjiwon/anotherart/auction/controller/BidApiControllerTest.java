package com.sjiwon.anotherart.auction.controller;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.controller.dto.request.BidRequest;
import com.sjiwon.anotherart.auction.controller.utils.BidRequestUtils;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.ObjectMapperUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.AuctionFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auction [Controller Layer] -> BidApiController 테스트")
class BidApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("경매 작품 입찰 테스트 [POST /api/auction/{auctionId}/bid]")
    class bid {
        private static final String BASE_URL = "/api/auction/{auctionId}/bid";

        @Test
        @DisplayName("Authorization 헤더에 Access Token이 없음에 따라 예외가 발생한다")
        void test1() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long auctionId = 1L;
            Auction auction = createMockAuction(art, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findById(auctionId)).willReturn(auction);

            final int bidAmount = art.getPrice() + 5_000;
            BidRequest request = BidRequestUtils.createRequest(bidAmount);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .content(ObjectMapperUtils.objectToJson(request));

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
                                    "AuctionApi/Bid/Failure/Case1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidAmount").description("입찰 금액")
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
        @DisplayName("아직 시작하지 않았거나 이미 종료된 경매에 대해서 입찰을 진행함에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long auctionId = 1L;
            Auction auction = createMockAuction(art, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
            given(auctionFindService.findById(auctionId)).willReturn(auction);

            Long bidderId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(bidderId);

            final int bidAmount = art.getPrice() + 5_000;
            BidRequest request = BidRequestUtils.createRequest(bidAmount);
            doThrow(AnotherArtException.type(AuctionErrorCode.AUCTION_NOT_START_OR_ALREADY_FINISHED))
                    .when(bidService)
                    .bid(auctionId, bidderId, bidAmount);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.AUCTION_NOT_START_OR_ALREADY_FINISHED;
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
                                    "AuctionApi/Bid/Failure/Case2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidAmount").description("입찰 금액")
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
        @DisplayName("경매 작품 소유자가 입찰을 진행함에 따라 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long ownerId = 1L;
            final String accessToken = jwtTokenProvider.createAccessToken(ownerId);

            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long auctionId = 1L;
            Auction auction = createMockAuction(art, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findById(auctionId)).willReturn(auction);

            final int bidAmount = art.getPrice() + 5_000;
            BidRequest request = BidRequestUtils.createRequest(bidAmount);
            doThrow(AnotherArtException.type(AuctionErrorCode.INVALID_OWNER_BID))
                    .when(bidService)
                    .bid(auctionId, ownerId, bidAmount);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.INVALID_OWNER_BID;
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
                                    "AuctionApi/Bid/Failure/Case3",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidAmount").description("입찰 금액")
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
        @DisplayName("입찰 금액이 이전 최고 입찰가보다 같거나 작음에 따라 예외가 발생한다")
        void test4() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long auctionId = 1L;
            Auction auction = createMockAuction(art, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findById(auctionId)).willReturn(auction);

            Long bidderId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(bidderId);

            final int bidAmount = art.getPrice();
            BidRequest request = BidRequestUtils.createRequest(bidAmount);
            doThrow(AnotherArtException.type(AuctionErrorCode.INVALID_BID_PRICE))
                    .when(bidService)
                    .bid(auctionId, bidderId, bidAmount);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.INVALID_BID_PRICE;
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
                                    "AuctionApi/Bid/Failure/Case4",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidAmount").description("입찰 금액")
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
        @DisplayName("이전 최고 입찰자가 연속적으로 입찰을 진행함에 따라 예외가 발생한다")
        void test5() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long auctionId = 1L;
            Auction auction = createMockAuction(art, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findById(auctionId)).willReturn(auction);

            Long bidderId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(bidderId);

            final int bidAmount1 = art.getPrice() + 5_000;
            BidRequest request1 = BidRequestUtils.createRequest(bidAmount1);
            final int bidAmount2 = bidAmount1 + 5_000;
            BidRequest request2 = BidRequestUtils.createRequest(bidAmount2);
            doThrow(AnotherArtException.type(AuctionErrorCode.INVALID_DUPLICATE_BID))
                    .when(bidService)
                    .bid(auctionId, bidderId, bidAmount2);

            // when
            MockHttpServletRequestBuilder requestBuilder1 = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request1));
            mockMvc.perform(requestBuilder1);

            MockHttpServletRequestBuilder requestBuilder2 = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request2));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.INVALID_DUPLICATE_BID;
            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "AuctionApi/Bid/Failure/Case5",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidAmount").description("입찰 금액")
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
        @DisplayName("입찰에 성공한다")
        void test6() throws Exception {
            // given
            Long artId = 1L;
            Art art = createMockArt(HASHTAGS);
            given(artFindService.findById(artId)).willReturn(art);

            Long auctionId = 1L;
            Auction auction = createMockAuction(art, currentTime1DayLater, currentTime3DayLater);
            given(auctionFindService.findById(auctionId)).willReturn(auction);

            Long bidderId = 2L;
            final String accessToken = jwtTokenProvider.createAccessToken(bidderId);

            final int bidAmount = art.getPrice() + 5_000;
            BidRequest request = BidRequestUtils.createRequest(bidAmount);
            doNothing()
                    .when(bidService)
                    .bid(auctionId, bidderId, bidAmount);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionId)
                    .contentType(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .content(ObjectMapperUtils.objectToJson(request));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist())
                    .andDo(
                            document(
                                    "AuctionApi/Bid/Success",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION).description("Access Token")
                                    ),
                                    pathParameters(
                                            parameterWithName("auctionId").description("입찰을 진행할 경매 ID(PK)")
                                    ),
                                    requestFields(
                                            fieldWithPath("bidAmount").description("입찰 금액")
                                    )
                            )
                    );
        }
    }

    private Art createMockArt(List<String> hashtags) {
        return ArtFixture.A.toArt(MemberFixture.A.toMember(), hashtags);
    }

    private Auction createMockAuction(Art art, LocalDateTime startDate, LocalDateTime endDate) {
        return AuctionFixture.toAuction(art, startDate, endDate);
    }
}
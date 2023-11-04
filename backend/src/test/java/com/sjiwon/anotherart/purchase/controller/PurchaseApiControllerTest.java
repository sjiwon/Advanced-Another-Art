package com.sjiwon.anotherart.purchase.controller;

import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Purchase [Controller Layer] -> PurchaseApiController 테스트")
class PurchaseApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("작품 구매 API [POST /api/arts/{artId}/purchase] - AccessToken 필수")
    class purchaseArt {
        private static final String BASE_URL = "/api/arts/{artId}/purchase";
        private static final Long ART_ID = 1L;

        @Nested
        @DisplayName("일반 작품 구매")
        class purchaseGeneralArt {
            @Test
            @WithMockUser
            @DisplayName("작품 소유자는 본인 작품을 구매할 수 없다")
            void throwExceptionByArtOwnerCannotPurchaseOwn() throws Exception {
                // given
                doThrow(AnotherArtException.type(PurchaseErrorCode.ART_OWNER_CANNOT_PURCHASE_OWN))
                        .when(purchaseService)
                        .purchaseArt(any(), any());

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final PurchaseErrorCode expectedError = PurchaseErrorCode.ART_OWNER_CANNOT_PURCHASE_OWN;
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
                                        "PurchaseApi/GeneralArt/Failure/Case1",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        ),
                                        getExceptionResponseFiels()
                                )
                        );
            }

            @Test
            @WithMockUser
            @DisplayName("작품이 이미 판매되었으면 구매할 수 없다")
            void throwExceptionByAlreadySold() throws Exception {
                // given
                doThrow(AnotherArtException.type(PurchaseErrorCode.ALREADY_SOLD))
                        .when(purchaseService)
                        .purchaseArt(any(), any());

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final PurchaseErrorCode expectedError = PurchaseErrorCode.ALREADY_SOLD;
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
                                        "PurchaseApi/GeneralArt/Failure/Case2",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        ),
                                        getExceptionResponseFiels()
                                )
                        );
            }

            @Test
            @WithMockUser
            @DisplayName("작품을 구매한다")
            void success() throws Exception {
                // given
                given(purchaseService.purchaseArt(any(), any())).willReturn(1L);

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                mockMvc.perform(requestBuilder)
                        .andExpect(status().isNoContent())
                        .andDo(
                                document(
                                        "PurchaseApi/GeneralArt/Success",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        )
                                )
                        );
            }
        }

        @Nested
        @DisplayName("경매 작품 구매")
        class purchaseAuctionArt {
            @Test
            @WithMockUser
            @DisplayName("경매가 종료되지 않았다면 구매할 수 없다")
            void throwExceptionByAuctionNotFinished() throws Exception {
                // given
                doThrow(AnotherArtException.type(PurchaseErrorCode.AUCTION_NOT_FINISHED))
                        .when(purchaseService)
                        .purchaseArt(any(), any());

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final PurchaseErrorCode expectedError = PurchaseErrorCode.AUCTION_NOT_FINISHED;
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
                                        "PurchaseApi/AuctionArt/Failure/Case1",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        ),
                                        getExceptionResponseFiels()
                                )
                        );
            }

            @Test
            @WithMockUser
            @DisplayName("낙찰자가 아니면 구매할 수 없다")
            void throwExceptionByBuyerIsNotHighestBidder() throws Exception {
                // given
                doThrow(AnotherArtException.type(PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER))
                        .when(purchaseService)
                        .purchaseArt(any(), any());

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final PurchaseErrorCode expectedError = PurchaseErrorCode.BUYER_IS_NOT_HIGHEST_BIDDER;
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
                                        "PurchaseApi/AuctionArt/Failure/Case2",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        ),
                                        getExceptionResponseFiels()
                                )
                        );
            }

            @Test
            @WithMockUser
            @DisplayName("작품이 이미 판매되었으면 구매할 수 없다")
            void throwExceptionByAlreadySold() throws Exception {
                // given
                doThrow(AnotherArtException.type(PurchaseErrorCode.ALREADY_SOLD))
                        .when(purchaseService)
                        .purchaseArt(any(), any());

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                final PurchaseErrorCode expectedError = PurchaseErrorCode.ALREADY_SOLD;
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
                                        "PurchaseApi/AuctionArt/Failure/Case3",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        ),
                                        getExceptionResponseFiels()
                                )
                        );
            }

            @Test
            @WithMockUser
            @DisplayName("작품을 구매한다")
            void success() throws Exception {
                // given
                given(purchaseService.purchaseArt(any(), any())).willReturn(1L);

                // when
                final MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                        .post(BASE_URL, ART_ID)
                        .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

                // then
                mockMvc.perform(requestBuilder)
                        .andExpect(status().isNoContent())
                        .andDo(
                                document(
                                        "PurchaseApi/AuctionArt/Success",
                                        getDocumentRequest(),
                                        getDocumentResponse(),
                                        getHeaderWithAccessToken(),
                                        pathParameters(
                                                parameterWithName("artId").description("구매할 작품 ID(PK)")
                                        )
                                )
                        );
            }
        }
    }
}

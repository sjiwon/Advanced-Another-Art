package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.service.dto.response.GeneralArt;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.common.utils.BasicArtBuilder;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtApiController 작품 단건 조회 테스트")
class ArtSearchApiControllerSimpleArtTest extends ControllerTest {
    private static final ArtFixture GENERAL_ART = ArtFixture.B;
    private static final ArtFixture AUCTION_ART = ArtFixture.A;
    private static final List<String> DEFAULT_HASHTAGS = HASHTAGS;

    @Nested
    @DisplayName("작품 단건 조회 테스트 [GET /api/arts/{artId}]")
    class searchArt {
        private static final String BASE_URL = "/api/arts/{artId}";

        @Test
        @DisplayName("작품 1건을 조회한다 (일반 작품)")
        void test1() throws Exception {
            // given
            BasicGeneralArt art = BasicArtBuilder.createBasicGeneralArt(GENERAL_ART);
            List<Long> likeMarkingMembers = List.of(10L, 20L, 30L, 40L, 50L);

            Long artId = 1L;
            GeneralArt response = GeneralArt.builder()
                    .art(art)
                    .hashtags(DEFAULT_HASHTAGS)
                    .likeMarkingMembers(likeMarkingMembers)
                    .build();
            given(artSimpleSearchService.getSingleArt(artId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, artId);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.art").exists())
                    .andExpect(jsonPath("$.result.hashtags").exists())
                    .andExpect(jsonPath("$.result.hashtags.size()").value(DEFAULT_HASHTAGS.size()))
                    .andExpect(jsonPath("$.result.likeMarkingMembers").exists())
                    .andExpect(jsonPath("$.result.likeMarkingMembers.size()").value(likeMarkingMembers.size()))
                    .andDo(
                            document(
                                    "ArtSearchApi/SingleArt/Success/General",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("조회할 작품 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result.art.artId").description("일반 작품 ID(PK)"),
                                            fieldWithPath("result.art.artName").description("일반 작품명"),
                                            fieldWithPath("result.art.artDescription").description("일반 작품 설명"),
                                            fieldWithPath("result.art.artPrice").description("일반 작품 가격"),
                                            fieldWithPath("result.art.artStatus").description("작품 상태 (판매 중 / 판매 완료)"),
                                            fieldWithPath("result.art.artRegistrationDate").description("일반 작품 등록 날짜"),
                                            fieldWithPath("result.art.artStorageName").description("일반 작품 서버 저장명(UUID)"),
                                            fieldWithPath("result.art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("result.art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("result.art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("result.art.buyerId").description("작품 구매자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("result.art.buyerNickname").description("작품 구매자 닉네임 -> 없으면 null"),
                                            fieldWithPath("result.art.buyerSchool").description("작품 구매자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("result.hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("result.likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("작품 1건을 조회한다 (경매 작품)")
        void test2() throws Exception {
            // given
            BasicAuctionArt art = BasicArtBuilder.createBasicAuctionArt(AUCTION_ART);
            List<Long> likeMarkingMembers = List.of(10L, 20L, 30L, 40L, 50L);
            int bidCount = 15;

            Long artId = 1L;
            AuctionArt response = AuctionArt.builder()
                    .art(art)
                    .hashtags(DEFAULT_HASHTAGS)
                    .likeMarkingMembers(likeMarkingMembers)
                    .bidCount(bidCount)
                    .build();
            given(artSimpleSearchService.getSingleArt(artId)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, artId);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.art").exists())
                    .andExpect(jsonPath("$.result.hashtags").exists())
                    .andExpect(jsonPath("$.result.hashtags.size()").value(DEFAULT_HASHTAGS.size()))
                    .andExpect(jsonPath("$.result.likeMarkingMembers").exists())
                    .andExpect(jsonPath("$.result.likeMarkingMembers.size()").value(likeMarkingMembers.size()))
                    .andExpect(jsonPath("$.result.bidCount").exists())
                    .andExpect(jsonPath("$.result.bidCount").value(bidCount))
                    .andDo(
                            document(
                                    "ArtSearchApi/SingleArt/Success/Auction",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("조회할 작품 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result.art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("result.art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("result.art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("result.art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("result.art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("result.art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("result.art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("result.art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("result.art.artName").description("경매 작품명"),
                                            fieldWithPath("result.art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("result.art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("result.art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("result.art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("result.art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("result.art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("result.art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("result.hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("result.likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("result.bidCount").description("경매 작품 입찰 횟수")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("존재하지 않는 작품에 대해서 조회를 하게되면 예외가 발생한다")
        void test3() throws Exception {
            // given
            Long artId = 1L;
            given(artSimpleSearchService.getSingleArt(artId)).willThrow(AnotherArtException.type(ArtErrorCode.ART_NOT_FOUND));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, artId);

            // then
            final ArtErrorCode expectedError = ArtErrorCode.ART_NOT_FOUND;
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.statusCode").exists())
                    .andExpect(jsonPath("$.statusCode").value(expectedError.getStatus().value()))
                    .andExpect(jsonPath("$.errorCode").exists())
                    .andExpect(jsonPath("$.errorCode").value(expectedError.getErrorCode()))
                    .andExpect(jsonPath("$.message").exists())
                    .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                    .andDo(
                            document(
                                    "ArtSearchApi/SingleArt/Failure",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("artId").description("조회할 작품 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                            fieldWithPath("errorCode").description("커스텀 예외 코드"),
                                            fieldWithPath("message").description("예외 메시지")
                                    )
                            )
                    );
        }
    }
}
package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicAuction;
import com.sjiwon.anotherart.art.infra.query.dto.response.GeneralArt;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.member.infra.query.dto.response.BasicMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.ArtFixture.GENERAL_1;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtSearchApiController 테스트")
class ArtSearchApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("작품 단건 조회 API [GET /api/arts/{artId}]")
    class getArt {

        private static final String BASE_URL = "/api/arts/{artId}";
        private static final Long ART_ID = 1L;

        @Test
        @DisplayName("단건 경매 작품을 조회한다")
        void getAuctionArt() throws Exception {
            // given
            AuctionArt response = generateAuctionArtResponse();
            given(artSearchService.getArt(ART_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, ART_ID);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/Basic/Auction",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("artId").description("조회할 작품 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result.auction.id").description("경매 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result.auction.highestBidPrice").description("경매 낙찰가")
                                                    .optional(),
                                            fieldWithPath("result.auction.startDate").description("경매 시작날짜")
                                                    .optional(),
                                            fieldWithPath("result.auction.endDate").description("경매 종료날짜")
                                                    .optional(),
                                            fieldWithPath("result.auction.bidCount").description("경매 입찰 횟수")
                                                    .optional(),
                                            fieldWithPath("result.art.id").description("경매 작품 ID(PK)"),
                                            fieldWithPath("result.art.name").description("경매 작품명"),
                                            fieldWithPath("result.art.description").description("경매 작품 설명"),
                                            fieldWithPath("result.art.price").description("경매 작품 초기 가격"),
                                            fieldWithPath("result.art.status").description("경매 작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result.art.storageName").description("경매 작품 이미지 경로"),
                                            fieldWithPath("result.art.registrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("result.art.hashtags").description("경매 작품 해시태그"),
                                            fieldWithPath("result.owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result.owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result.owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result.highestBidder.id").description("작품 입찰자/낙찰자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result.highestBidder.nickname").description("작품 입찰자/낙찰자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result.highestBidder.school").description("작품 입찰자/낙찰자 학교")
                                                    .optional()
                                    )
                            )
                    );
        }
        
        @Test
        @DisplayName("단건 일반 작품을 조회한다")
        void getGeneralArt() throws Exception {
            // given
            GeneralArt response = generateGeneralArtResponse();
            given(artSearchService.getArt(ART_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, ART_ID);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/Basic/General",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    pathParameters(
                                            parameterWithName("artId").description("조회할 작품 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result.art.id").description("경매 작품 ID(PK)"),
                                            fieldWithPath("result.art.name").description("경매 작품명"),
                                            fieldWithPath("result.art.description").description("경매 작품 설명"),
                                            fieldWithPath("result.art.price").description("경매 작품 초기 가격"),
                                            fieldWithPath("result.art.status").description("경매 작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result.art.storageName").description("경매 작품 이미지 경로"),
                                            fieldWithPath("result.art.registrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("result.art.hashtags").description("경매 작품 해시태그"),
                                            fieldWithPath("result.owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result.owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result.owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result.buyer.id").description("작품 구매자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result.buyer.nickname").description("작품 구매자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result.buyer.school").description("작품 구매자 학교")
                                                    .optional()
                                    )
                            )
                    );
        }
    }

    private AuctionArt generateAuctionArtResponse() {
        return new AuctionArt(
                new BasicAuction(
                        1L,
                        235_000,
                        LocalDateTime.now().minusDays(5),
                        LocalDateTime.now().minusDays(1),
                        13
                ),
                new BasicArt(
                        1L,
                        AUCTION_1.getName(),
                        AUCTION_1.getDescription(),
                        AUCTION_1.getPrice(),
                        ON_SALE.getDescription(),
                        AUCTION_1.getStorageName(),
                        LocalDateTime.now().minusDays(5),
                        List.of("해시태그1", "해시태그2", "해시태그3")
                ),
                new BasicMember(1L, "작품소유자", "서울대학교"),
                new BasicMember(2L, "경매입찰자or낙찰자", "경기대학교")
        );
    }

    private GeneralArt generateGeneralArtResponse() {
        return new GeneralArt(
                new BasicArt(
                        1L,
                        GENERAL_1.getName(),
                        GENERAL_1.getDescription(),
                        GENERAL_1.getPrice(),
                        ON_SALE.getDescription(),
                        GENERAL_1.getStorageName(),
                        LocalDateTime.now().minusDays(5),
                        List.of("해시태그1", "해시태그2", "해시태그3")
                ),
                new BasicMember(1L, "작품소유자", "서울대학교"),
                new BasicMember(2L, "작품구매자", "경기대학교")
        );
    }
}

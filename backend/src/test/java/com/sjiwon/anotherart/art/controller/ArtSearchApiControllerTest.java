package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.infra.query.dto.response.*;
import com.sjiwon.anotherart.art.service.dto.response.ArtAssembler;
import com.sjiwon.anotherart.art.utils.search.Pagination;
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
import static com.sjiwon.anotherart.fixture.ArtFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
                                            fieldWithPath("result.art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result.art.name").description("작품명"),
                                            fieldWithPath("result.art.description").description("작품 설명"),
                                            fieldWithPath("result.art.price").description("작품 초기 가격"),
                                            fieldWithPath("result.art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result.art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result.art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result.art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result.art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
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
                                            fieldWithPath("result.art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result.art.name").description("작품명"),
                                            fieldWithPath("result.art.description").description("작품 설명"),
                                            fieldWithPath("result.art.price").description("작품 가격"),
                                            fieldWithPath("result.art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result.art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result.art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result.art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result.art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
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

    @Nested
    @DisplayName("현재 경매 진행중인 작품 조회 API [GET /api/arts]")
    class getActiveAuctionArts {
        private static final String BASE_URL = "/api/arts";

        @Test
        @DisplayName("현재 경매가 진행중인 작품들을 조회한다")
        void getAuctionArt() throws Exception {
            // given
            ArtAssembler response = generateAuctionArtAssemblerResponse();
            given(artSearchService.getActiveAuctionArts(any(), any())).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("sortType", "rdate")
                    .param("page", String.valueOf(1));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/ActiveAuction",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("sortType").description("정렬 기준")
                                                    .attributes(constraint("date-rdate / price-rprice / like-rlike / count-rcount")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("첫번째 페이지는 1부터 시작"))
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].auction.id").description("경매 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].auction.highestBidPrice").description("경매 낙찰가")
                                                    .optional(),
                                            fieldWithPath("result[].auction.startDate").description("경매 시작날짜")
                                                    .optional(),
                                            fieldWithPath("result[].auction.endDate").description("경매 종료날짜")
                                                    .optional(),
                                            fieldWithPath("result[].auction.bidCount").description("경매 입찰 횟수")
                                                    .optional(),
                                            fieldWithPath("result[].art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result[].art.name").description("작품명"),
                                            fieldWithPath("result[].art.description").description("작품 설명"),
                                            fieldWithPath("result[].art.price").description("작품 초기 가격"),
                                            fieldWithPath("result[].art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result[].art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result[].art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result[].art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("result[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result[].highestBidder.id").description("작품 입찰자/낙찰자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].highestBidder.nickname").description("작품 입찰자/낙찰자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result[].highestBidder.school").description("작품 입찰자/낙찰자 학교")
                                                    .optional(),

                                            fieldWithPath("pagination.totalElements").description("전체 데이터 수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("현재 페이지 범위 첫번째 번호"),
                                            fieldWithPath("pagination.rangeEndNumber").description("현재 페이지 범위 마지막 번호"),
                                            fieldWithPath("pagination.prevExists").description("이전 페이지 존재 여부 [10 page 단위"),
                                            fieldWithPath("pagination.nextExists").description("다음 페이지 존재 여부 [10 page 단위]")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("키워드 작품 조회 API [GET /api/arts/keyword]")
    class getArtsByKeyword {
        private static final String BASE_URL = "/api/arts/keyword";

        @Test
        @DisplayName("해당 키워드가 포함된 경매 작품을 조회한다")
        void getAuctionArtsByKeyword() throws Exception {
            // given
            ArtAssembler response = generateAuctionArtAssemblerResponse();
            given(artSearchService.getArtsByKeyword(any(), any())).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("sortType", "rdate")
                    .param("page", String.valueOf(1))
                    .param("artType", "auction")
                    .param("keyword", "Hello");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/Keyword/AuctionArt",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("sortType").description("정렬 기준")
                                                    .attributes(constraint("date-rdate / price-rprice / like-rlike / count-rcount")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("첫번째 페이지는 1부터 시작")),
                                            parameterWithName("artType").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("keyword").description("검색 키워드")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].auction.id").description("경매 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].auction.highestBidPrice").description("경매 낙찰가")
                                                    .optional(),
                                            fieldWithPath("result[].auction.startDate").description("경매 시작날짜")
                                                    .optional(),
                                            fieldWithPath("result[].auction.endDate").description("경매 종료날짜")
                                                    .optional(),
                                            fieldWithPath("result[].auction.bidCount").description("경매 입찰 횟수")
                                                    .optional(),
                                            fieldWithPath("result[].art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result[].art.name").description("작품명"),
                                            fieldWithPath("result[].art.description").description("작품 설명"),
                                            fieldWithPath("result[].art.price").description("작품 초기 가격"),
                                            fieldWithPath("result[].art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result[].art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result[].art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result[].art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("result[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result[].highestBidder.id").description("작품 입찰자/낙찰자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].highestBidder.nickname").description("작품 입찰자/낙찰자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result[].highestBidder.school").description("작품 입찰자/낙찰자 학교")
                                                    .optional(),

                                            fieldWithPath("pagination.totalElements").description("전체 데이터 수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("현재 페이지 범위 첫번째 번호"),
                                            fieldWithPath("pagination.rangeEndNumber").description("현재 페이지 범위 마지막 번호"),
                                            fieldWithPath("pagination.prevExists").description("이전 페이지 존재 여부 [10 page 단위"),
                                            fieldWithPath("pagination.nextExists").description("다음 페이지 존재 여부 [10 page 단위]")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("해당 키워드가 포함된 일반 작품을 조회한다")
        void getGeneralArtsByKeyword() throws Exception {
            // given
            ArtAssembler response = generateGeneralArtAssemblerResponse();
            given(artSearchService.getArtsByKeyword(any(), any())).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("sortType", "rdate")
                    .param("page", String.valueOf(1))
                    .param("artType", "general")
                    .param("keyword", "Hello");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/Keyword/GeneralArt",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("sortType").description("정렬 기준")
                                                    .attributes(constraint("date-rdate / price-rprice / like-rlike / count-rcount")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("첫번째 페이지는 1부터 시작")),
                                            parameterWithName("artType").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("keyword").description("검색 키워드")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result[].art.name").description("작품명"),
                                            fieldWithPath("result[].art.description").description("작품 설명"),
                                            fieldWithPath("result[].art.price").description("작품 초기 가격"),
                                            fieldWithPath("result[].art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result[].art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result[].art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result[].art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("result[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result[].buyer.id").description("작품 구매자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].buyer.nickname").description("작품 구매자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result[].buyer.school").description("작품 구매자 학교")
                                                    .optional(),

                                            fieldWithPath("pagination.totalElements").description("전체 데이터 수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("현재 페이지 범위 첫번째 번호"),
                                            fieldWithPath("pagination.rangeEndNumber").description("현재 페이지 범위 마지막 번호"),
                                            fieldWithPath("pagination.prevExists").description("이전 페이지 존재 여부 [10 page 단위"),
                                            fieldWithPath("pagination.nextExists").description("다음 페이지 존재 여부 [10 page 단위]")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("해시태그 작품 조회 API [GET /api/arts/hashtag]")
    class getArtsByHashtag {
        private static final String BASE_URL = "/api/arts/hashtag";

        @Test
        @DisplayName("해당 해시태그가 포함된 경매 작품을 조회한다")
        void getAuctionArtsByKeyword() throws Exception {
            // given
            ArtAssembler response = generateAuctionArtAssemblerResponse();
            given(artSearchService.getArtsByHashtag(any(), any())).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("sortType", "rdate")
                    .param("page", String.valueOf(1))
                    .param("artType", "auction")
                    .param("hashtag", "A");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/Hashtag/AuctionArt",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("sortType").description("정렬 기준")
                                                    .attributes(constraint("date-rdate / price-rprice / like-rlike / count-rcount")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("첫번째 페이지는 1부터 시작")),
                                            parameterWithName("artType").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("hashtag").description("검색 해시태그")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].auction.id").description("경매 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].auction.highestBidPrice").description("경매 낙찰가")
                                                    .optional(),
                                            fieldWithPath("result[].auction.startDate").description("경매 시작날짜")
                                                    .optional(),
                                            fieldWithPath("result[].auction.endDate").description("경매 종료날짜")
                                                    .optional(),
                                            fieldWithPath("result[].auction.bidCount").description("경매 입찰 횟수")
                                                    .optional(),
                                            fieldWithPath("result[].art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result[].art.name").description("작품명"),
                                            fieldWithPath("result[].art.description").description("작품 설명"),
                                            fieldWithPath("result[].art.price").description("작품 초기 가격"),
                                            fieldWithPath("result[].art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result[].art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result[].art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result[].art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("result[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result[].highestBidder.id").description("작품 입찰자/낙찰자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].highestBidder.nickname").description("작품 입찰자/낙찰자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result[].highestBidder.school").description("작품 입찰자/낙찰자 학교")
                                                    .optional(),

                                            fieldWithPath("pagination.totalElements").description("전체 데이터 수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("현재 페이지 범위 첫번째 번호"),
                                            fieldWithPath("pagination.rangeEndNumber").description("현재 페이지 범위 마지막 번호"),
                                            fieldWithPath("pagination.prevExists").description("이전 페이지 존재 여부 [10 page 단위"),
                                            fieldWithPath("pagination.nextExists").description("다음 페이지 존재 여부 [10 page 단위]")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("해당 해시태그가 포함된 일반 작품을 조회한다")
        void getGeneralArtsByKeyword() throws Exception {
            // given
            ArtAssembler response = generateGeneralArtAssemblerResponse();
            given(artSearchService.getArtsByHashtag(any(), any())).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .param("sortType", "rdate")
                    .param("page", String.valueOf(1))
                    .param("artType", "general")
                    .param("hashtag", "A");

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "ArtApi/Search/Hashtag/GeneralArt",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    requestParameters(
                                            parameterWithName("sortType").description("정렬 기준")
                                                    .attributes(constraint("date-rdate / price-rprice / like-rlike / count-rcount")),
                                            parameterWithName("page").description("현재 페이지")
                                                    .attributes(constraint("첫번째 페이지는 1부터 시작")),
                                            parameterWithName("artType").description("작품 타입")
                                                    .attributes(constraint("general / auction")),
                                            parameterWithName("hashtag").description("검색 해시태그")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].art.id").description("작품 ID(PK)"),
                                            fieldWithPath("result[].art.name").description("작품명"),
                                            fieldWithPath("result[].art.description").description("작품 설명"),
                                            fieldWithPath("result[].art.price").description("작품 초기 가격"),
                                            fieldWithPath("result[].art.status").description("작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result[].art.storageName").description("작품 이미지 경로"),
                                            fieldWithPath("result[].art.registrationDate").description("작품 등록 날짜"),
                                            fieldWithPath("result[].art.hashtags").description("작품 해시태그"),
                                            fieldWithPath("result[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("result[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result[].buyer.id").description("작품 구매자 ID(PK)")
                                                    .optional(),
                                            fieldWithPath("result[].buyer.nickname").description("작품 구매자 닉네임")
                                                    .optional(),
                                            fieldWithPath("result[].buyer.school").description("작품 구매자 학교")
                                                    .optional(),

                                            fieldWithPath("pagination.totalElements").description("전체 데이터 수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("현재 페이지 범위 첫번째 번호"),
                                            fieldWithPath("pagination.rangeEndNumber").description("현재 페이지 범위 마지막 번호"),
                                            fieldWithPath("pagination.prevExists").description("이전 페이지 존재 여부 [10 page 단위"),
                                            fieldWithPath("pagination.nextExists").description("다음 페이지 존재 여부 [10 page 단위]")
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
                        List.of("해시태그1", "해시태그2", "해시태그3"),
                        List.of(1L, 2L, 3L)
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
                        LocalDateTime.now().minusDays(6),
                        List.of("해시태그1", "해시태그2", "해시태그3"),
                        List.of(1L, 2L, 3L)
                ),
                new BasicMember(1L, "작품소유자", "서울대학교"),
                new BasicMember(2L, "작품구매자", "경기대학교")
        );
    }

    private ArtAssembler generateAuctionArtAssemblerResponse() {
        List<ArtDetails> auctionArts = List.of(
                new AuctionArt(
                        new BasicAuction(
                                1L,
                                235_000,
                                LocalDateTime.now().minusDays(3),
                                LocalDateTime.now().plusDays(10),
                                13
                        ),
                        new BasicArt(
                                1L,
                                AUCTION_1.getName(),
                                AUCTION_1.getDescription(),
                                AUCTION_1.getPrice(),
                                ON_SALE.getDescription(),
                                AUCTION_1.getStorageName(),
                                LocalDateTime.now().minusDays(4),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "경매입찰자or낙찰자", "경기대학교")
                ),
                new AuctionArt(
                        new BasicAuction(
                                2L,
                                135_000,
                                LocalDateTime.now().minusDays(4),
                                LocalDateTime.now().plusDays(3),
                                4
                        ),
                        new BasicArt(
                                2L,
                                AUCTION_2.getName(),
                                AUCTION_2.getDescription(),
                                AUCTION_2.getPrice(),
                                ON_SALE.getDescription(),
                                AUCTION_2.getStorageName(),
                                LocalDateTime.now().minusDays(5),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "경매입찰자or낙찰자", "경기대학교")
                ),
                new AuctionArt(
                        new BasicAuction(
                                3L,
                                55_000,
                                LocalDateTime.now().minusDays(6),
                                LocalDateTime.now().plusDays(2),
                                6
                        ),
                        new BasicArt(
                                3L,
                                AUCTION_3.getName(),
                                AUCTION_3.getDescription(),
                                AUCTION_3.getPrice(),
                                ON_SALE.getDescription(),
                                AUCTION_3.getStorageName(),
                                LocalDateTime.now().minusDays(7),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "경매입찰자or낙찰자", "경기대학교")
                )
        );
        Pagination pagination = Pagination.of((long) auctionArts.size(), 1, 1);

        return new ArtAssembler(auctionArts, pagination);
    }

    private ArtAssembler generateGeneralArtAssemblerResponse() {
        List<ArtDetails> generalArts = List.of(
                new GeneralArt(
                        new BasicArt(
                                1L,
                                GENERAL_1.getName(),
                                GENERAL_1.getDescription(),
                                GENERAL_1.getPrice(),
                                ON_SALE.getDescription(),
                                GENERAL_1.getStorageName(),
                                LocalDateTime.now().minusDays(4),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "작품구매자", "경기대학교")
                ),
                new GeneralArt(
                        new BasicArt(
                                2L,
                                GENERAL_2.getName(),
                                GENERAL_2.getDescription(),
                                GENERAL_2.getPrice(),
                                ON_SALE.getDescription(),
                                GENERAL_2.getStorageName(),
                                LocalDateTime.now().minusDays(5),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "작품구매자", "경기대학교")
                ),
                new GeneralArt(
                        new BasicArt(
                                3L,
                                GENERAL_3.getName(),
                                GENERAL_3.getDescription(),
                                GENERAL_3.getPrice(),
                                ON_SALE.getDescription(),
                                GENERAL_3.getStorageName(),
                                LocalDateTime.now().minusDays(7),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "작품구매자", "경기대학교")
                )
        );
        Pagination pagination = Pagination.of((long) generalArts.size(), 1, 1);

        return new ArtAssembler(generalArts, pagination);
    }
}

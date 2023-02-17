package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.sjiwon.anotherart.art.controller.utils.BasicArtBuilder.createAuctionArtList;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtApiController 현재 경매중인 작품 조회 테스트")
class ArtSearchApiControllerComplexActiveAuctionArtTest extends ControllerTest {
    // 총 작품 12건에 대한 Fetching
    private static final int TOTAL_ELEMENTS = 12;
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final int PAGE_0 = 1;
    private static final int PAGE_1 = 2;
    private static final Pageable DEFAULT_PAGE_REQUEST_0 = PageRequest.of(PAGE_0 - 1, DEFAULT_PAGE_SIZE); // 8건
    private static final Pageable DEFAULT_PAGE_REQUEST_1 = PageRequest.of(PAGE_1 - 1, DEFAULT_PAGE_SIZE); // 4건
    private static final String DATE_ASC = "date";
    private static final String DATE_DESC = "rdate";
    private static final String PRICE_ASC = "price";
    private static final String PRICE_DESC = "rprice";
    private static final String FAVORITE_COUNT_ASC = "like";
    private static final String FAVORITE_COUNT_DESC = "rlike";
    private static final String BID_COUNT_ASC = "count";
    private static final String BID_COUNT_DESC = "rcount";
    private static final List<String> COMMON_NAME_AND_DESCRIPTION = List.of(
            "hello1", "world1", "hello2", "world2", "hello3", "world3",
            "hello4", "world4", "hello5", "world5", "hello6", "world6"
    );

    @Nested
    @DisplayName("현재 경매중인 작품 조회 테스트 [GET /api/arts/main]")
    class searchArt {
        private static final String BASE_URL = "/api/arts/main";

        @Test
        @DisplayName("기본으로 제공되지 않는 정렬 기준은 빈 리스트가 반환된다")
        void test1() throws Exception {
            // given
            final String sort = "anonymous";
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(new ArrayList<>(), DEFAULT_PAGE_REQUEST_0, () -> 0L));

            // when - then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(0))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.artList").isEmpty())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(0))
                    .andExpect(jsonPath("$.pagination.totalPages").value(0))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/Empty",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList").description("현재 페이지의 작품 리스트 (단건 조회 REST Docs 참고)"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("등록 날짜 기준 오름차순 [date]")
        void test2() throws Exception {
            // given
            final String sort = "date";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByDate(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/Date/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/Date/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("등록 날짜 기준 내림차순 [rdate]")
        void test3() throws Exception {
            // given
            final String sort = "rdate";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByReverseDate(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReverseDate/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReverseDate/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("입찰 가격 기준 오름차순 [price]")
        void test4() throws Exception {
            // given
            final String sort = "price";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByPrice(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/Price/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/Price/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("입찰 가격 기준 내림차순 [rprice]")
        void test5() throws Exception {
            // given
            final String sort = "rprice";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByReversePrice(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReversePrice/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReversePrice/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("작품 좋아요 횟수 기준 오름차순 [like]")
        void test6() throws Exception {
            // given
            final String sort = "like";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByFavoriteCount(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/FavoriteCount/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/FavoriteCount/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("작품 좋아요 횟수 기준 내림차순 [rlike]")
        void test7() throws Exception {
            // given
            final String sort = "rlike";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByReverseFavoriteCount(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReverseFavoriteCount/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReverseFavoriteCount/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("입찰 횟수 기준 오름차순 [count]")
        void test8() throws Exception {
            // given
            final String sort = "count";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByBidCount(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/BidCount/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/BidCount/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }

        @Test
        @DisplayName("입찰 횟수 기준 내림차순 [rcount]")
        void test9() throws Exception {
            // given
            final String sort = "rcount";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION);
            sortByReverseBidCount(auctionArtList);

            List<AuctionArt> auctionArtList1 = IntStream.range(0, 8)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_0))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList1, DEFAULT_PAGE_REQUEST_0, () -> TOTAL_ELEMENTS));

            List<AuctionArt> auctionArtList2 = IntStream.range(8, 12)
                    .mapToObj(auctionArtList::get)
                    .toList();
            given(auctionArtComplexSearchService.getCurrentActiveAuctionArtList(sort, DEFAULT_PAGE_REQUEST_1))
                    .willReturn(PageableExecutionUtils.getPage(auctionArtList2, DEFAULT_PAGE_REQUEST_1, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder1 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_0));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList1.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReverseBidCount/Page1",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );

            MockHttpServletRequestBuilder requestBuilder2 = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("sort", sort)
                    .param("page", String.valueOf(PAGE_1));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(auctionArtList2.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(TOTAL_ELEMENTS))
                    .andExpect(jsonPath("$.pagination.totalPages").value(2))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/ActiveAuctionArt/ReverseBidCount/Page2",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.auctionId").description("경매 ID(PK)"),
                                            fieldWithPath("artList[].art.highestBidPrice").description("현재 경매 입찰가"),
                                            fieldWithPath("artList[].art.auctionStartDate").description("경매 시작 날짜"),
                                            fieldWithPath("artList[].art.auctionEndDate").description("경매 종료 날짜"),
                                            fieldWithPath("artList[].art.highestBidderId").description("최고 입찰자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderNickname").description("최고 입찰자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.highestBidderSchool").description("최고 입찰자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].art.artId").description("경매 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("경매 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("경매 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("경매 작품 초기 가격"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("경매 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
                                            fieldWithPath("artList[].bidCount").description("경매 작품 입찰 횟수"),
                                            fieldWithPath("pagination.totalElements").description("전체 데이터 개수"),
                                            fieldWithPath("pagination.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("pagination.currentPage").description("현재 페이지"),
                                            fieldWithPath("pagination.rangeStartNumber").description("하단 페이징바의 첫번째 페이지"),
                                            fieldWithPath("pagination.rangeEndNumber").description("하단 페이징바의 마지막 페이지"),
                                            fieldWithPath("pagination.prev").description("이전 페이지 존재 여부 (10개 단위)"),
                                            fieldWithPath("pagination.next").description("다음 페이지 존재 여부 (10개 단위)")
                                    )
                            )
                    );
        }
    }

    private void sortByDate(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getArt().getArtRegistrationDate().isBefore(o2.getArt().getArtRegistrationDate())) {
                return -1;
            } else if (o1.getArt().getArtRegistrationDate().isAfter(o2.getArt().getArtRegistrationDate())) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByReverseDate(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getArt().getArtRegistrationDate().isAfter(o2.getArt().getArtRegistrationDate())) {
                return -1;
            } else if (o1.getArt().getArtRegistrationDate().isBefore(o2.getArt().getArtRegistrationDate())) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByPrice(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getArt().getHighestBidPrice() < o2.getArt().getHighestBidPrice()) {
                return -1;
            } else if (o1.getArt().getHighestBidPrice() > o2.getArt().getHighestBidPrice()) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByReversePrice(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getArt().getHighestBidPrice() > o2.getArt().getHighestBidPrice()) {
                return -1;
            } else if (o1.getArt().getHighestBidPrice() < o2.getArt().getHighestBidPrice()) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByFavoriteCount(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getLikeMarkingMembers().size() < o2.getLikeMarkingMembers().size()) {
                return -1;
            } else if (o1.getLikeMarkingMembers().size() > o2.getLikeMarkingMembers().size()) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByReverseFavoriteCount(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getLikeMarkingMembers().size() > o2.getLikeMarkingMembers().size()) {
                return -1;
            } else if (o1.getLikeMarkingMembers().size() < o2.getLikeMarkingMembers().size()) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByBidCount(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getBidCount() < o2.getBidCount()) {
                return -1;
            } else if (o1.getBidCount() > o2.getBidCount()) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortByReverseBidCount(List<AuctionArt> auctionArts) {
        auctionArts.sort((o1, o2) -> {
            if (o1.getBidCount() > o2.getBidCount()) {
                return -1;
            } else if (o1.getBidCount() < o2.getBidCount()) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }
}
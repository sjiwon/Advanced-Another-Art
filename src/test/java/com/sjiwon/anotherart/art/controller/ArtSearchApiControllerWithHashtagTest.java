package com.sjiwon.anotherart.art.controller;

import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.service.dto.response.GeneralArt;
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

import static com.sjiwon.anotherart.common.utils.BasicArtBuilder.createAuctionArtList;
import static com.sjiwon.anotherart.common.utils.BasicArtBuilder.createGeneralArtList;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Art [Controller Layer] -> ArtApiController 키워드를 통한 작품 조회 테스트")
public class ArtSearchApiControllerWithHashtagTest extends ControllerTest {
    // 총 작품 12건에 대한 Fetching (hello - world 조건에 따라 각각 6건씩 Fetching)
    private static final int TOTAL_ELEMENTS = 12;
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, DEFAULT_PAGE_SIZE); // 6건
    private static final String TYPE_GENERAL = "general";
    private static final String TYPE_AUCTION = "auction";
    private static final List<String> COMMON_NAME_AND_DESCRIPTION = List.of(
            "hello1", "world1", "hello2", "world2", "hello3", "world3",
            "hello4", "world4", "hello5", "world5", "hello6", "world6"
    );
    private static final String HASHTAG_A = "A";
    private static final List<String> HASHTAG_CONTAIN_A = List.of("A", "C", "D", "E", "F");
    private static final List<String> HASHTAG_CONTAIN_B = List.of("B", "C", "D", "E", "F");
    private static final List<List<String>> COMMON_HASHTAG = List.of(
            HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B,
            HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B
    );

    @Nested
    @DisplayName("해시태그를 통한 작품 조회 테스트 [GET /api/arts/hashtag]")
    class searchArt {
        private static final String BASE_URL = "/api/arts/hashtag";

        @Test
        @DisplayName("기본으로 제공되지 않는 정렬 기준은 빈 리스트가 반환된다")
        void test1() throws Exception {
            // given
            final String sort = "anonymous";
            given(auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, sort, DEFAULT_PAGE_REQUEST))
                    .willReturn(PageableExecutionUtils.getPage(new ArrayList<>(), DEFAULT_PAGE_REQUEST, () -> 0L));

            // when - then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("hashtag", HASHTAG_A)
                    .param("type", TYPE_AUCTION)
                    .param("sort", sort)
                    .param("page", String.valueOf(1));

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
                                    "ArtSearchApi/HashtagSearch/Empty",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("hashtag").description("검색할 해시태그"),
                                            parameterWithName("type").description("작품 검색 타입 [general / auction]"),
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
        @DisplayName("등록 날짜 기준 오름차순 [hashtag=A / type=auction / sort=date / page=1]")
        void test2() throws Exception {
            // given
            final String sort = "date";
            List<AuctionArt> auctionArtList = createAuctionArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION, COMMON_HASHTAG);
            sortAuctionArtByDate(auctionArtList);

            List<AuctionArt> filteringAuctionArtWithHashtag = filteringAuctionArtWithHashtag(auctionArtList, HASHTAG_A);
            given(auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, sort, DEFAULT_PAGE_REQUEST))
                    .willReturn(PageableExecutionUtils.getPage(filteringAuctionArtWithHashtag, DEFAULT_PAGE_REQUEST, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("hashtag", HASHTAG_A)
                    .param("type", TYPE_AUCTION)
                    .param("sort", sort)
                    .param("page", String.valueOf(1));

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(filteringAuctionArtWithHashtag.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(6))
                    .andExpect(jsonPath("$.pagination.totalPages").value(1))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/HashtagSearch/Auction",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("hashtag").description("검색할 해시태그"),
                                            parameterWithName("type").description("작품 검색 타입 [general / auction]"),
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
                                            fieldWithPath("artList[].art.artStatus").description("경매 작품 상태 (판매 중 / 판매 완료)"),
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
        @DisplayName("등록 날짜 기준 오름차순 [hashtag=A / type=general / sort=date / page=1]")
        void test3() throws Exception {
            // given
            final String sort = "date";
            List<GeneralArt> generalArtList = createGeneralArtList(TOTAL_ELEMENTS, COMMON_NAME_AND_DESCRIPTION, COMMON_HASHTAG);
            sortGeneralArtByDate(generalArtList);

            List<GeneralArt> filteringGeneralArtWithHashtag = filteringGeneralArtWithHashtag(generalArtList, HASHTAG_A);
            given(generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, sort, DEFAULT_PAGE_REQUEST))
                    .willReturn(PageableExecutionUtils.getPage(filteringGeneralArtWithHashtag, DEFAULT_PAGE_REQUEST, () -> TOTAL_ELEMENTS));

            // when - then
            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                    .get(BASE_URL)
                    .param("hashtag", HASHTAG_A)
                    .param("type", TYPE_GENERAL)
                    .param("sort", sort)
                    .param("page", String.valueOf(1));

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.contentSize").exists())
                    .andExpect(jsonPath("$.contentSize").value(filteringGeneralArtWithHashtag.size()))
                    .andExpect(jsonPath("$.artList").exists())
                    .andExpect(jsonPath("$.pagination").exists())
                    .andExpect(jsonPath("$.pagination.totalElements").value(6))
                    .andExpect(jsonPath("$.pagination.totalPages").value(1))
                    .andExpect(jsonPath("$.pagination.prev").value(false))
                    .andExpect(jsonPath("$.pagination.next").value(false))
                    .andDo(
                            document(
                                    "ArtSearchApi/HashtagSearch/General",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestParameters(
                                            parameterWithName("hashtag").description("검색할 해시태그"),
                                            parameterWithName("type").description("작품 검색 타입 [general / auction]"),
                                            parameterWithName("sort").description("정렬 기준 [(r)date / (r)price / (r)like / (r)count]"),
                                            parameterWithName("page").description("현재 페이지 (1부터 시작)")
                                    ),
                                    responseFields(
                                            fieldWithPath("contentSize").description("현재 페이지의 작품 리스트 개수"),
                                            fieldWithPath("artList[].art.artId").description("일반 작품 ID(PK)"),
                                            fieldWithPath("artList[].art.artName").description("일반 작품명"),
                                            fieldWithPath("artList[].art.artDescription").description("일반 작품 설명"),
                                            fieldWithPath("artList[].art.artPrice").description("일반 작품 가격"),
                                            fieldWithPath("artList[].art.artStatus").description("작품 상태 (판매 중 / 판매 완료)"),
                                            fieldWithPath("artList[].art.artRegistrationDate").description("일반 작품 등록 날짜"),
                                            fieldWithPath("artList[].art.artStorageName").description("일반 작품 서버 저장명(UUID)"),
                                            fieldWithPath("artList[].art.ownerId").description("작품 주인 ID(PK)"),
                                            fieldWithPath("artList[].art.ownerNickname").description("작품 주인 닉네임"),
                                            fieldWithPath("artList[].art.ownerSchool").description("작품 주인 재학중인 학교"),
                                            fieldWithPath("artList[].art.buyerId").description("작품 구매자 ID(PK) -> 없으면 null"),
                                            fieldWithPath("artList[].art.buyerNickname").description("작품 구매자 닉네임 -> 없으면 null"),
                                            fieldWithPath("artList[].art.buyerSchool").description("작품 구매자 재학중인 학교 -> 없으면 null"),
                                            fieldWithPath("artList[].hashtags").description("작품의 해시태그 리스트"),
                                            fieldWithPath("artList[].likeMarkingMembers").description("해당 작품을 좋아요 등록한 사용자 ID 리스트"),
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

    private List<AuctionArt> filteringAuctionArtWithHashtag(List<AuctionArt> auctionArtList, String hashtag) {
        return auctionArtList.stream()
                .filter(auctionArt -> auctionArt.getHashtags().contains(hashtag))
                .toList();
    }

    private List<GeneralArt> filteringGeneralArtWithHashtag(List<GeneralArt> generalArtList, String hashtag) {
        return generalArtList.stream()
                .filter(generalArt -> generalArt.getHashtags().contains(hashtag))
                .toList();
    }

    private void sortAuctionArtByDate(List<AuctionArt> auctionArtList) {
        auctionArtList.sort((o1, o2) -> {
            if (o1.getArt().getArtRegistrationDate().isBefore(o2.getArt().getArtRegistrationDate())) {
                return -1;
            } else if (o1.getArt().getArtRegistrationDate().isAfter(o2.getArt().getArtRegistrationDate())) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }

    private void sortGeneralArtByDate(List<GeneralArt> generalArtList) {
        generalArtList.sort((o1, o2) -> {
            if (o1.getArt().getArtRegistrationDate().isAfter(o2.getArt().getArtRegistrationDate())) {
                return -1;
            } else if (o1.getArt().getArtRegistrationDate().isBefore(o2.getArt().getArtRegistrationDate())) {
                return 1;
            } else {
                return Long.compare(o1.getArt().getArtId(), o2.getArt().getArtId());
            }
        });
    }
}

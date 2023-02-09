package com.sjiwon.anotherart.auction.controller;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.auction.exception.AuctionErrorCode;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
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
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);

            Member bidder = createMemberB();
            final int bidAmount = art.getPrice() + 5_000;

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .param("bidAmount", String.valueOf(bidAmount));

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
                                    requestParameters(
                                            parameterWithName("bidAmount").description("입찰 금액")
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
        @DisplayName("경매가 종료된 후 입찰을 진행함에 따라 예외가 발생한다")
        void test2() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);
            ReflectionTestUtils.setField(auction.getPeriod(), "startDate", LocalDateTime.now().minusDays(2));
            ReflectionTestUtils.setField(auction.getPeriod(), "endDate", LocalDateTime.now().minusDays(1));

            Member bidder = createMemberB();
            final int bidAmount = art.getPrice() + 5_000;

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(bidder.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("bidAmount", String.valueOf(bidAmount));

            // then
            final AuctionErrorCode expectedError = AuctionErrorCode.AUCTION_ALREADY_FINISHED;
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
                                    requestParameters(
                                            parameterWithName("bidAmount").description("입찰 금액")
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
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);

            final int bidAmount = art.getPrice() + 5_000;

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(owner.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("bidAmount", String.valueOf(bidAmount));

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
                                    requestParameters(
                                            parameterWithName("bidAmount").description("입찰 금액")
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
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);

            Member bidder = createMemberB();
            final int bidAmount = art.getPrice();

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(bidder.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("bidAmount", String.valueOf(bidAmount));

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
                                    requestParameters(
                                            parameterWithName("bidAmount").description("입찰 금액")
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
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);

            Member bidder = createMemberB();
            final int bidAmount1 = art.getPrice() + 5_000;
            final int bidAmount2 = bidAmount1 + 5_000;

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(bidder.getId());
            MockHttpServletRequestBuilder requestBuilder1 = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("bidAmount", String.valueOf(bidAmount1));
            mockMvc.perform(requestBuilder1);

            MockHttpServletRequestBuilder requestBuilder2 = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("bidAmount", String.valueOf(bidAmount2));

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
                                    requestParameters(
                                            parameterWithName("bidAmount").description("입찰 금액")
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
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);

            Member bidder = createMemberB();
            final int bidAmount = art.getPrice() + 5_000;

            // when
            final String accessToken = jwtTokenProvider.createAccessToken(bidder.getId());
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken)
                    .param("bidAmount", String.valueOf(bidAmount));

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
                                    requestParameters(
                                            parameterWithName("bidAmount").description("입찰 금액")
                                    )
                            )
                    );

            // 경매 입찰 레코드 확인
            List<AuctionRecord> auctionRecord = auctionRecordRepository.findByAuctionId(auction.getId());
            assertThat(auctionRecord.size()).isEqualTo(1);
            assertThat(auctionRecord.get(0).getBidAmount()).isEqualTo(bidAmount);
            assertThat(auctionRecord.get(0).getBidder().getId()).isEqualTo(bidder.getId());
            assertThat(auctionRecord.get(0).getBidder().getName()).isEqualTo(bidder.getName());
            assertThat(auctionRecord.get(0).getBidder().getNickname()).isEqualTo(bidder.getNickname());

            // 경매 정보 확인
            Auction findAuction = auctionRepository.findByArtId(art.getId()).orElseThrow();
            assertThat(findAuction.getBidder()).isNotNull();
            assertThat(findAuction.getBidder().getId()).isEqualTo(bidder.getId());
            assertThat(findAuction.getBidder().getName()).isEqualTo(bidder.getName());
            assertThat(findAuction.getBidder().getNickname()).isEqualTo(bidder.getNickname());
            assertThat(findAuction.getBidAmount()).isEqualTo(bidAmount);

            // 입찰자 정보 확인
            Member findBidder = memberRepository.findById(bidder.getId()).orElseThrow();
            assertThat(findBidder.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - bidAmount);
        }

        @Test
        @DisplayName("입찰에 성공한다 -> 입찰 2회 진행")
        void test7() throws Exception {
            // given
            Member owner = createMemberA();
            Art art = createAuctionArt(owner);
            Auction auction = initAuction(art);

            // 첫번째 입찰 진행
            Member bidder1 = createMemberB();
            final int bidAmount1 = art.getPrice() + 5_000;

            final String accessToken1 = jwtTokenProvider.createAccessToken(bidder1.getId());
            MockHttpServletRequestBuilder requestBuilder1 = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken1)
                    .param("bidAmount", String.valueOf(bidAmount1));

            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist());

            // 첫번째 입찰 정보 확인
            List<AuctionRecord> auctionRecord1 = auctionRecordRepository.findByAuctionId(auction.getId());
            assertThat(auctionRecord1.size()).isEqualTo(1);
            assertThat(auctionRecord1.get(0).getBidAmount()).isEqualTo(bidAmount1);
            assertThat(auctionRecord1.get(0).getBidder().getId()).isEqualTo(bidder1.getId());
            assertThat(auctionRecord1.get(0).getBidder().getName()).isEqualTo(bidder1.getName());
            assertThat(auctionRecord1.get(0).getBidder().getNickname()).isEqualTo(bidder1.getNickname());

            Auction findAuction1 = auctionRepository.findByArtId(art.getId()).orElseThrow();
            assertThat(findAuction1.getBidder()).isNotNull();
            assertThat(findAuction1.getBidder().getId()).isEqualTo(bidder1.getId());
            assertThat(findAuction1.getBidder().getName()).isEqualTo(bidder1.getName());
            assertThat(findAuction1.getBidder().getNickname()).isEqualTo(bidder1.getNickname());
            assertThat(findAuction1.getBidAmount()).isEqualTo(bidAmount1);

            Member findBidder = memberRepository.findById(bidder1.getId()).orElseThrow();
            assertThat(findBidder.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - bidAmount1);
 
            // 두번째 입찰 진행
            Member bidder2 = createMemberC();
            final int bidAmount2 = bidAmount1 + 5_000;

            final String accessToken2 = jwtTokenProvider.createAccessToken(bidder2.getId());
            MockHttpServletRequestBuilder requestBuilder2 = RestDocumentationRequestBuilders
                    .post(BASE_URL, auction.getId())
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken2)
                    .param("bidAmount", String.valueOf(bidAmount2));

            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isNoContent())
                    .andExpect(jsonPath("$").doesNotExist());

            // 두번째 입찰 정보 확인
            List<AuctionRecord> auctionRecord2 = auctionRecordRepository.findByAuctionId(auction.getId());
            assertThat(auctionRecord2.size()).isEqualTo(2);
            assertThat(auctionRecord2.get(0).getBidAmount()).isEqualTo(bidAmount1);
            assertThat(auctionRecord2.get(0).getBidder().getId()).isEqualTo(bidder1.getId());
            assertThat(auctionRecord2.get(0).getBidder().getName()).isEqualTo(bidder1.getName());
            assertThat(auctionRecord2.get(0).getBidder().getNickname()).isEqualTo(bidder1.getNickname());
            assertThat(auctionRecord2.get(1).getBidAmount()).isEqualTo(bidAmount2);
            assertThat(auctionRecord2.get(1).getBidder().getId()).isEqualTo(bidder2.getId());
            assertThat(auctionRecord2.get(1).getBidder().getName()).isEqualTo(bidder2.getName());
            assertThat(auctionRecord2.get(1).getBidder().getNickname()).isEqualTo(bidder2.getNickname());

            Auction findAuction2 = auctionRepository.findByArtId(art.getId()).orElseThrow();
            assertThat(findAuction2.getBidder()).isNotNull();
            assertThat(findAuction2.getBidder().getId()).isEqualTo(bidder2.getId());
            assertThat(findAuction2.getBidder().getName()).isEqualTo(bidder2.getName());
            assertThat(findAuction2.getBidder().getNickname()).isEqualTo(bidder2.getNickname());
            assertThat(findAuction2.getBidAmount()).isEqualTo(bidAmount2);

            Member findBidder1 = memberRepository.findById(bidder1.getId()).orElseThrow();
            Member findBidder2 = memberRepository.findById(bidder2.getId()).orElseThrow();
            assertThat(findBidder1.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT);
            assertThat(findBidder2.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - bidAmount2);
        }
    }

    private Member createMemberA() {
        Member member = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Member createMemberB() {
        Member member = memberRepository.save(MemberFixture.B.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Member createMemberC() {
        Member member = memberRepository.save(MemberFixture.C.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return member;
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
    }
}
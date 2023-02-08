package com.sjiwon.anotherart.purchase.controller;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtStatus;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.security.exception.AuthErrorCode;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.purchase.exception.PurchaseErrorCode;
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
            Member owner = createMemberA();
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt);

            // 입찰 진행
            Member highestBidder = createMemberB();
            final int bidAmount = auctionArt.getPrice() + 5_000;
            callBidApi(auction, highestBidder, bidAmount);

            // 경매 강제 종료
            makeAuctionFinished(auction);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionArt.getId());

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
        @DisplayName("아직 종료되지 않은 경매 작품은 구매할 수 없고 예외가 발생한다")
        void test2() throws Exception {
            // given
            Member owner = createMemberA();
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt);

            // 입찰 진행
            Member highestBidder = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(highestBidder.getId());
            final int bidAmount = auctionArt.getPrice() + 5_000;
            callBidApi(auction, highestBidder, bidAmount);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionArt.getId())
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
        @DisplayName("종료된 경매에 대해서 최고 입찰자가 아닌 사용자가 구매 요청을 하면 예외가 발생한다")
        void test3() throws Exception {
            // given
            Member owner = createMemberA();
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt);

            // 입찰 진행
            Member highestBidder = createMemberB();
            final int bidAmount = auctionArt.getPrice() + 5_000;
            callBidApi(auction, highestBidder, bidAmount);

            // 경매 강제 종료
            makeAuctionFinished(auction);

            // 다른 사용자의 구매 요청
            Member member = createMemberC();
            String accessToken = jwtTokenProvider.createAccessToken(member.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionArt.getId())
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
        @DisplayName("이미 판매 완료된 경매 작품에 대한 고의적인 API 호출로 구매 요청을 진행할 경우 예외가 발생한다")
        void test4() throws Exception {
            // given
            Member owner = createMemberA();
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt);

            // 입찰 진행
            Member highestBidder = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(highestBidder.getId());
            final int bidAmount = auctionArt.getPrice() + 5_000;
            callBidApi(auction, highestBidder, bidAmount);

            // 경매 강제 종료 & 판매 완료 설정
            makeAuctionFinished(auction);
            auctionArt.changeArtStatus(ArtStatus.SOLD_OUT);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionArt.getId())
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
        @DisplayName("입찰된 경매 작품에 대해서 구매 확정을 진행할 때 사용 가능한 포인트가 부족함에 따라 예외가 발생한다")
        void test5() throws Exception {
            // given
            Member owner = createMemberA();
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt);

            // 입찰 진행
            Member highestBidder = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(highestBidder.getId());
            final int bidAmount = auctionArt.getPrice() + 5_000;
            callBidApi(auction, highestBidder, bidAmount);

            // 경매 강제 종료 & 사용자 포인트 감소
            makeAuctionFinished(auction);
            highestBidder.decreasePoint(highestBidder.getAvailablePoint());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionArt.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT;
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
        @DisplayName("경매 작품 구매에 성공한다")
        void test6() throws Exception {
            // given
            Member owner = createMemberA();
            Art auctionArt = createAuctionArt(owner);
            Auction auction = initAuction(auctionArt);

            // 입찰 진행
            Member highestBidder = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(highestBidder.getId());
            final int bidAmount = auctionArt.getPrice() + 5_000;
            callBidApi(auction, highestBidder, bidAmount);

            // 경매 강제 종료
            makeAuctionFinished(auction);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, auctionArt.getId())
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

            // 작품 판매 확인
            Art findArt = artRepository.findById(auctionArt.getId()).orElseThrow();
            assertThat(findArt.isSoldOut()).isTrue();

            // 사용자 포인트 확인
            Member findMember = memberRepository.findById(highestBidder.getId()).orElseThrow();
            assertThat(findMember.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - bidAmount);

            Member findOwner = memberRepository.findById(owner.getId()).orElseThrow();
            assertThat(findOwner.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT + bidAmount);

            // 경매 기록 확인
            List<AuctionRecord> auctionRecords = auctionRecordRepository.findByAuctionId(auction.getId());
            assertThat(auctionRecords.size()).isEqualTo(1);
            assertThat(auctionRecords.get(0).getBidder().getId()).isEqualTo(highestBidder.getId());
            assertThat(auctionRecords.get(0).getBidAmount()).isEqualTo(bidAmount);
        }

        @Test
        @DisplayName("이미 판매 완료된 일반 작품에 대한 고의적인 API 호출로 구매 요청을 진행할 경우 예외가 발생한다")
        void test7() throws Exception {
            // given
            Member owner = createMemberA();
            Art generalArt = createGeneralArt(owner);
            
            // 판매 완료 설정
            generalArt.changeArtStatus(ArtStatus.SOLD_OUT);
            
            Member purchaser = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(purchaser.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, generalArt.getId())
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
        void test8() throws Exception {
            // given
            Member owner = createMemberA();
            Art generalArt = createGeneralArt(owner);

            // 사용자 포인트 감소
            Member purchaser = createMemberB();
            purchaser.decreasePoint(purchaser.getAvailablePoint());
            String accessToken = jwtTokenProvider.createAccessToken(purchaser.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, generalArt.getId())
                    .header(AUTHORIZATION, BEARER_TOKEN + accessToken);

            // then
            final PurchaseErrorCode expectedError = PurchaseErrorCode.INSUFFICIENT_AVAILABLE_POINT;
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
        void test9() throws Exception {
            // given
            Member owner = createMemberA();
            Art generalArt = createGeneralArt(owner);

            Member purchaser = createMemberB();
            String accessToken = jwtTokenProvider.createAccessToken(purchaser.getId());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL, generalArt.getId())
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

            // 작품 판매 확인
            Art findArt = artRepository.findById(generalArt.getId()).orElseThrow();
            assertThat(findArt.isSoldOut()).isTrue();

            // 사용자 포인트 확인
            Member findMember = memberRepository.findById(purchaser.getId()).orElseThrow();
            assertThat(findMember.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT - generalArt.getPrice());

            Member findOwner = memberRepository.findById(owner.getId()).orElseThrow();
            assertThat(findOwner.getAvailablePoint()).isEqualTo(INIT_AVAILABLE_POINT + generalArt.getPrice());
        }
    }

    private void makeAuctionFinished(Auction auction) {
        ReflectionTestUtils.setField(auction.getPeriod(), "startDate", LocalDateTime.now().minusDays(2));
        ReflectionTestUtils.setField(auction.getPeriod(), "endDate", LocalDateTime.now().minusDays(1));
    }

    private void callBidApi(Auction auction, Member highestBidder, int bidAmount) throws Exception {
        String highestBidderAccessToken = jwtTokenProvider.createAccessToken(highestBidder.getId());

        MockHttpServletRequestBuilder bidRequestBuilder = RestDocumentationRequestBuilders
                .post("/api/auction/{auctionId}/bid", auction.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .header(AUTHORIZATION, BEARER_TOKEN + highestBidderAccessToken)
                .param("bidAmount", String.valueOf(bidAmount));

        mockMvc.perform(bidRequestBuilder);
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

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner, HASHTAGS));
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
    }
}
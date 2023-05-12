package com.sjiwon.anotherart.member.controller;

import com.sjiwon.anotherart.art.infra.query.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicArt;
import com.sjiwon.anotherart.art.infra.query.dto.response.BasicAuction;
import com.sjiwon.anotherart.common.ControllerTest;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.point.PointType;
import com.sjiwon.anotherart.member.infra.query.dto.response.BasicMember;
import com.sjiwon.anotherart.member.infra.query.dto.response.MemberPointRecord;
import com.sjiwon.anotherart.member.infra.query.dto.response.TradedArt;
import com.sjiwon.anotherart.member.service.dto.response.MemberInformation;
import com.sjiwon.anotherart.member.service.dto.response.PointRecordAssembler;
import com.sjiwon.anotherart.member.service.dto.response.TradedArtAssembler;
import com.sjiwon.anotherart.member.service.dto.response.WinningAuctionArtAssembler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.sjiwon.anotherart.art.domain.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.ArtStatus.SOLD;
import static com.sjiwon.anotherart.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.sjiwon.anotherart.common.utils.TokenUtils.BEARER_TOKEN;
import static com.sjiwon.anotherart.fixture.ArtFixture.*;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member [Controller Layer] -> MemberInformationApiController 테스트")
class MemberInformationApiControllerTest extends ControllerTest {
    @Nested
    @DisplayName("사용자 기본 정보 조회 API [GET /api/members/{memberId}] - AccessToken 필수")
    class getInformation {
        private static final String BASE_URL = "/api/members/{memberId}";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("사용자 기본 정보를 조회한다")
        void success() throws Exception {
            // given
            MemberInformation response = generateMemberInformationResponse();
            given(memberInformationService.getInformation(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/Basic",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("id").description("사용자 ID(PK)"),
                                            fieldWithPath("name").description("사용자 이름"),
                                            fieldWithPath("nickname").description("사용자 닉네임"),
                                            fieldWithPath("loginId").description("사용자 로그인 아이디"),
                                            fieldWithPath("school").description("사용자 재학중인 학교"),
                                            fieldWithPath("phone").description("사용자 전화번호"),
                                            fieldWithPath("email").description("사용자 이메일"),
                                            fieldWithPath("address.postcode").description("우편번호"),
                                            fieldWithPath("address.defaultAddress").description("기본 주소"),
                                            fieldWithPath("address.detailAddress").description("상세 주소"),
                                            fieldWithPath("totalPoint").description("전체 포인트"),
                                            fieldWithPath("availablePoint").description("사용 가능한 포인트")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 포인트 활용 내역 조회 API [GET /api/members/{memberId}/points] - AccessToken 필수")
    class getPointRecords {
        private static final String BASE_URL = "/api/members/{memberId}/points";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("포인트 활용 내역을 조회한다")
        void success() throws Exception {
            // given
            PointRecordAssembler response = generatePointRecords();
            given(memberInformationService.getPointRecords(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/PointRecord",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].pointType").description("활용 타입"),
                                            fieldWithPath("result[].amount").description("활용 금액"),
                                            fieldWithPath("result[].recordDate").description("활용 날짜")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자의 낙찰된 경매 작품 조회 API [GET /api/members/{memberId}/winning-auctions] - AccessToken 필수")
    class getWinningAuctionArts {
        private static final String BASE_URL = "/api/members/{memberId}/winning-auctions";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("낙찰된 경매 작품을 조회한다")
        void success() throws Exception {
            // given
            WinningAuctionArtAssembler response = generateWinningAuctionArts();
            given(memberInformationService.getWinningAuctionArts(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/WinningAuction",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("result[].auction.id").description("경매 ID(PK)"),
                                            fieldWithPath("result[].auction.highestBidPrice").description("경매 낙찰가"),
                                            fieldWithPath("result[].auction.startDate").description("경매 시작날짜"),
                                            fieldWithPath("result[].auction.endDate").description("경매 종료날짜"),
                                            fieldWithPath("result[].auction.bidCount").description("경매 입찰 횟수"),
                                            fieldWithPath("result[].art.id").description("경매 작품 ID(PK)"),
                                            fieldWithPath("result[].art.name").description("경매 작품명"),
                                            fieldWithPath("result[].art.description").description("경매 작품 설명"),
                                            fieldWithPath("result[].art.price").description("경매 작품 초기 가격"),
                                            fieldWithPath("result[].art.status").description("경매 작품 상태 [판매중 / 판매 완료]"),
                                            fieldWithPath("result[].art.storageName").description("경매 작품 이미지 경로"),
                                            fieldWithPath("result[].art.registrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("result[].art.hashtags").description("경매 작품 해시태그"),
                                            fieldWithPath("result[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("result[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("result[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("result[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("result[].highestBidder.id").description("작품 낙찰자 ID(PK)"),
                                            fieldWithPath("result[].highestBidder.nickname").description("작품 낙찰자 닉네임"),
                                            fieldWithPath("result[].highestBidder.school").description("작품 낙찰자 학교")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 판매한 작품 조회 API [GET /api/members/{memberId}/arts/sold] - AccessToken 필수")
    class getSoldArts {
        private static final String BASE_URL = "/api/members/{memberId}/arts/sold";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("판매한 작품을 조회한다")
        void success() throws Exception {
            // given
            TradedArtAssembler response = generateTradedArts();
            given(memberInformationService.getSoldArts(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/SoldArt",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("tradedAuctions[].art.id").description("경매 작품 ID(PK)"),
                                            fieldWithPath("tradedAuctions[].art.name").description("경매 작품명"),
                                            fieldWithPath("tradedAuctions[].art.description").description("경매 작품 설명"),
                                            fieldWithPath("tradedAuctions[].art.price").description("경매 작품 판매 가격"),
                                            fieldWithPath("tradedAuctions[].art.status").description("경매 작품 상태 [판매 완료]"),
                                            fieldWithPath("tradedAuctions[].art.storageName").description("경매 작품 이미지 경로"),
                                            fieldWithPath("tradedAuctions[].art.registrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("tradedAuctions[].art.hashtags").description("경매 작품 해시태그"),
                                            fieldWithPath("tradedAuctions[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("tradedAuctions[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("tradedAuctions[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("tradedAuctions[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("tradedAuctions[].buyer.id").description("작품 구매자 ID(PK)"),
                                            fieldWithPath("tradedAuctions[].buyer.nickname").description("작품 구매자 닉네임"),
                                            fieldWithPath("tradedAuctions[].buyer.school").description("작품 구매자 학교"),

                                            fieldWithPath("tradedGenerals[].art.id").description("일반 작품 ID(PK)"),
                                            fieldWithPath("tradedGenerals[].art.name").description("일반 작품명"),
                                            fieldWithPath("tradedGenerals[].art.description").description("일반 작품 설명"),
                                            fieldWithPath("tradedGenerals[].art.price").description("일반 작품 판매 가격"),
                                            fieldWithPath("tradedGenerals[].art.status").description("일반 작품 상태 [판매 완료]"),
                                            fieldWithPath("tradedGenerals[].art.storageName").description("일반 작품 이미지 경로"),
                                            fieldWithPath("tradedGenerals[].art.registrationDate").description("일반 작품 등록 날짜"),
                                            fieldWithPath("tradedGenerals[].art.hashtags").description("일반 작품 해시태그"),
                                            fieldWithPath("tradedGenerals[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("tradedGenerals[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("tradedGenerals[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("tradedGenerals[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("tradedGenerals[].buyer.id").description("작품 구매자 ID(PK)"),
                                            fieldWithPath("tradedGenerals[].buyer.nickname").description("작품 구매자 닉네임"),
                                            fieldWithPath("tradedGenerals[].buyer.school").description("작품 구매자 학교")
                                    )
                            )
                    );
        }
    }

    @Nested
    @DisplayName("사용자가 구매한 작품 조회 API [GET /api/members/{memberId}/arts/purchase] - AccessToken 필수")
    class getPurchaseArts {
        private static final String BASE_URL = "/api/members/{memberId}/arts/purchase";
        private static final Long MEMBER_ID = 1L;

        @Test
        @WithMockUser
        @DisplayName("구매한 작품을 조회한다")
        void success() throws Exception {
            // given
            TradedArtAssembler response = generateTradedArts();
            given(memberInformationService.getPurchaseArts(MEMBER_ID)).willReturn(response);

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, MEMBER_ID)
                    .header(AUTHORIZATION, String.join(" ", BEARER_TOKEN, ACCESS_TOKEN));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "MemberApi/Information/PurchaseArt",
                                    getDocumentRequest(),
                                    getDocumentResponse(),
                                    getHeaderWithAccessToken(),
                                    pathParameters(
                                            parameterWithName("memberId").description("조회할 사용자 ID(PK)")
                                    ),
                                    responseFields(
                                            fieldWithPath("tradedAuctions[].art.id").description("경매 작품 ID(PK)"),
                                            fieldWithPath("tradedAuctions[].art.name").description("경매 작품명"),
                                            fieldWithPath("tradedAuctions[].art.description").description("경매 작품 설명"),
                                            fieldWithPath("tradedAuctions[].art.price").description("경매 작품 판매 가격"),
                                            fieldWithPath("tradedAuctions[].art.status").description("경매 작품 상태 [판매 완료]"),
                                            fieldWithPath("tradedAuctions[].art.storageName").description("경매 작품 이미지 경로"),
                                            fieldWithPath("tradedAuctions[].art.registrationDate").description("경매 작품 등록 날짜"),
                                            fieldWithPath("tradedAuctions[].art.hashtags").description("경매 작품 해시태그"),
                                            fieldWithPath("tradedAuctions[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("tradedAuctions[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("tradedAuctions[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("tradedAuctions[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("tradedAuctions[].buyer.id").description("작품 구매자 ID(PK)"),
                                            fieldWithPath("tradedAuctions[].buyer.nickname").description("작품 구매자 닉네임"),
                                            fieldWithPath("tradedAuctions[].buyer.school").description("작품 구매자 학교"),

                                            fieldWithPath("tradedGenerals[].art.id").description("일반 작품 ID(PK)"),
                                            fieldWithPath("tradedGenerals[].art.name").description("일반 작품명"),
                                            fieldWithPath("tradedGenerals[].art.description").description("일반 작품 설명"),
                                            fieldWithPath("tradedGenerals[].art.price").description("일반 작품 판매 가격"),
                                            fieldWithPath("tradedGenerals[].art.status").description("일반 작품 상태 [판매 완료]"),
                                            fieldWithPath("tradedGenerals[].art.storageName").description("일반 작품 이미지 경로"),
                                            fieldWithPath("tradedGenerals[].art.registrationDate").description("일반 작품 등록 날짜"),
                                            fieldWithPath("tradedGenerals[].art.hashtags").description("일반 작품 해시태그"),
                                            fieldWithPath("tradedGenerals[].art.likeMembers[]").description("작품 찜한 사용자 ID(PK) 리스트"),
                                            fieldWithPath("tradedGenerals[].owner.id").description("작품 소유자 ID(PK)"),
                                            fieldWithPath("tradedGenerals[].owner.nickname").description("작품 소유자 닉네임"),
                                            fieldWithPath("tradedGenerals[].owner.school").description("작품 소유자 학교"),
                                            fieldWithPath("tradedGenerals[].buyer.id").description("작품 구매자 ID(PK)"),
                                            fieldWithPath("tradedGenerals[].buyer.nickname").description("작품 구매자 닉네임"),
                                            fieldWithPath("tradedGenerals[].buyer.school").description("작품 구매자 학교")
                                    )
                            )
                    );
        }
    }

    private MemberInformation generateMemberInformationResponse() {
        Member member = createMember(MEMBER_A, 1L);
        return new MemberInformation(member);
    }

    private Member createMember(MemberFixture fixture, Long id) {
        Member member = fixture.toMember();
        ReflectionTestUtils.setField(member, "id", id);

        member.addPointRecords(CHARGE, 100_000);
        member.decreaseAvailablePoint(12_000);

        return member;
    }

    private PointRecordAssembler generatePointRecords() {
        List<MemberPointRecord> result = List.of(
                new MemberPointRecord(PURCHASE, 100_000, LocalDateTime.now().minusDays(1)),
                new MemberPointRecord(PURCHASE, 50_000, LocalDateTime.now().minusDays(2)),
                new MemberPointRecord(PointType.SOLD, 35_000, LocalDateTime.now().minusDays(3)),
                new MemberPointRecord(REFUND, 50_000, LocalDateTime.now().minusDays(4)),
                new MemberPointRecord(PURCHASE, 385_000, LocalDateTime.now().minusDays(5)),
                new MemberPointRecord(CHARGE, 1_000_000, LocalDateTime.now().minusDays(6))
        );

        return new PointRecordAssembler(result);
    }

    private WinningAuctionArtAssembler generateWinningAuctionArts() {
        List<AuctionArt> result = List.of(
                new AuctionArt(
                        new BasicAuction(
                                3L,
                                180_000,
                                LocalDateTime.now().minusDays(10),
                                LocalDateTime.now().minusDays(4),
                                8
                        ),
                        new BasicArt(
                                3L,
                                AUCTION_3.getName(),
                                AUCTION_3.getDescription(),
                                AUCTION_3.getPrice(),
                                ON_SALE.getDescription(),
                                AUCTION_3.getStorageName(),
                                LocalDateTime.now().minusDays(10),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "경매낙찰자", "경기대학교")
                ),
                new AuctionArt(
                        new BasicAuction(
                                2L,
                                380_000,
                                LocalDateTime.now().minusDays(7),
                                LocalDateTime.now().minusDays(2),
                                5
                        ),
                        new BasicArt(
                                2L,
                                AUCTION_2.getName(),
                                AUCTION_2.getDescription(),
                                AUCTION_2.getPrice(),
                                ON_SALE.getDescription(),
                                AUCTION_2.getStorageName(),
                                LocalDateTime.now().minusDays(7),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "경매낙찰자", "경기대학교")
                ),
                new AuctionArt(
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
                        new BasicMember(2L, "경매낙찰자", "경기대학교")
                )
        );

        return new WinningAuctionArtAssembler(result);
    }

    private TradedArtAssembler generateTradedArts() {
        List<TradedArt> tradedAuctions = List.of(
                new TradedArt(
                        new BasicArt(
                                2L,
                                AUCTION_2.getName(),
                                AUCTION_2.getDescription(),
                                AUCTION_2.getPrice(),
                                SOLD.getDescription(),
                                AUCTION_2.getStorageName(),
                                LocalDateTime.now().minusDays(7),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "작품구매자", "경기대학교")
                ),
                new TradedArt(
                        new BasicArt(
                                1L,
                                AUCTION_1.getName(),
                                AUCTION_1.getDescription(),
                                AUCTION_1.getPrice(),
                                SOLD.getDescription(),
                                AUCTION_1.getStorageName(),
                                LocalDateTime.now().minusDays(5),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "작품구매자", "경기대학교")
                )
        );

        List<TradedArt> tradedGenerals = List.of(
                new TradedArt(
                        new BasicArt(
                                1L,
                                GENERAL_1.getName(),
                                GENERAL_1.getDescription(),
                                GENERAL_1.getPrice(),
                                SOLD.getDescription(),
                                GENERAL_1.getStorageName(),
                                LocalDateTime.now().minusDays(5),
                                List.of("해시태그1", "해시태그2", "해시태그3"),
                                List.of(1L, 2L, 3L)
                        ),
                        new BasicMember(1L, "작품소유자", "서울대학교"),
                        new BasicMember(2L, "작품구매자", "경기대학교")
                )
        );

        return new TradedArtAssembler(tradedAuctions, tradedGenerals);
    }
}

package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtType;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.infra.query.dto.BasicGeneralArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
import com.sjiwon.anotherart.art.service.dto.response.GeneralArt;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecord;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Address;
import com.sjiwon.anotherart.member.domain.Email;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.Password;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static com.sjiwon.anotherart.art.domain.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.ArtType.GENERAL;
import static com.sjiwon.anotherart.common.utils.ArtUtils.currentTime3DayAgo;
import static com.sjiwon.anotherart.common.utils.ArtUtils.currentTime3DayLater;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Service Layer] -> ??????????????? ?????? ?????? ?????? ?????????")
@RequiredArgsConstructor
public class ArtComplexSearchServiceByHashtagTest extends ServiceIntegrateTest {
    private final GeneralArtComplexSearchService generalArtComplexSearchService;
    private final AuctionArtComplexSearchService auctionArtComplexSearchService;

    private static final int TOTAL_ELEMENTS = 12;
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, DEFAULT_PAGE_SIZE); // 6???
    private static final String DATE_ASC = "date";
    private static final String DATE_DESC = "rdate";
    private static final String PRICE_ASC = "price";
    private static final String PRICE_DESC = "rprice";
    private static final String FAVORITE_COUNT_ASC = "like";
    private static final String FAVORITE_COUNT_DESC = "rlike";
    private static final String BID_COUNT_ASC = "count";
    private static final String BID_COUNT_DESC = "rcount";
    private static final String HASHTAG_A = "A";
    private static final String HASHTAG_B = "B";
    private static final List<String> HASHTAG_CONTAIN_A = List.of("A", "C", "D", "E", "F");
    private static final List<String> HASHTAG_CONTAIN_B = List.of("B", "C", "D", "E", "F");
    private static final List<List<String>> COMMON_HASHTAG = List.of(
            HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B,
            HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B, HASHTAG_CONTAIN_A, HASHTAG_CONTAIN_B
    );

    @Nested
    @DisplayName("??????????????? ?????? ?????? ?????? ??????")
    class findGeneralArtListByKeyworkd {
        @Test
        @DisplayName("???????????? ???????????? ?????? ?????? ????????? ??? ???????????? ????????????")
        void test1() {
            // given
            final String ANONYMOUS = "anonymous";

            // when - then
            Page<GeneralArt> result1 = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, ANONYMOUS, DEFAULT_PAGE_REQUEST);
            Page<GeneralArt> result2 = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, ANONYMOUS, DEFAULT_PAGE_REQUEST);

            assertAll(
                    () -> assertThat(result1.getContent()).isEmpty(),
                    () -> assertThat(result1.getTotalElements()).isEqualTo(0),
                    () -> assertThat(result2.getContent()).isEmpty(),
                    () -> assertThat(result2.getTotalElements()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test2() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, GENERAL, COMMON_HASHTAG);

            // when - then
            Page<GeneralArt> resultByHashtagA = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, DATE_ASC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<GeneralArt> resultByHashtagB = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, DATE_ASC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test3() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, GENERAL, COMMON_HASHTAG);

            // when - then
            Page<GeneralArt> resultByHashtagA = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, DATE_DESC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<GeneralArt> resultByHashtagB = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, DATE_DESC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test4() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, GENERAL, COMMON_HASHTAG);

            // when - then
            Page<GeneralArt> resultByHashtagA = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, PRICE_ASC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtPrice)
                                    .toList()
                    ).containsExactly(1000, 3000, 5000, 7000, 9000, 11000),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<GeneralArt> resultByHashtagB = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, PRICE_ASC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtPrice)
                                    .toList()
                    ).containsExactly(2000, 4000, 6000, 8000, 10000, 12000),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test5() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, GENERAL, COMMON_HASHTAG);

            // when - then
            Page<GeneralArt> resultByHashtagA = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, PRICE_DESC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtPrice)
                                    .toList()
                    ).containsExactly(11000, 9000, 7000, 5000, 3000, 1000),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<GeneralArt> resultByHashtagB = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, PRICE_DESC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtPrice)
                                    .toList()
                    ).containsExactly(12000, 10000, 8000, 6000, 4000, 2000),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ????????? ?????? ?????? ????????????")
        void test6() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, GENERAL, COMMON_HASHTAG);
            proceedingLikeMarking(auctionArts);

            // when - then
            Page<GeneralArt> resultByHashtagA = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, FAVORITE_COUNT_ASC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(1, 3, 5, 7, 9, 11),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<GeneralArt> resultByHashtagB = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, FAVORITE_COUNT_ASC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(2, 4, 6, 8, 10, 12),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ????????? ?????? ?????? ????????????")
        void test7() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, GENERAL, COMMON_HASHTAG);
            proceedingLikeMarking(auctionArts);

            // when - then
            Page<GeneralArt> resultByHashtagA = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_A, FAVORITE_COUNT_DESC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(
                            content1.stream()
                                    .map(GeneralArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(11, 9, 7, 5, 3, 1),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<GeneralArt> resultByHashtagB = generalArtComplexSearchService.getGeneralArtListByHashtag(HASHTAG_B, FAVORITE_COUNT_DESC, DEFAULT_PAGE_REQUEST);
            List<GeneralArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getArt)
                                    .map(BasicGeneralArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(
                            content2.stream()
                                    .map(GeneralArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(12, 10, 8, 6, 4, 2),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }
    }

    @Nested
    @DisplayName("??????????????? ?????? ?????? ?????? ??????")
    class findAuctionArtListByKeyworkd {
        @Test
        @DisplayName("???????????? ???????????? ?????? ?????? ????????? ??? ???????????? ????????????")
        void test1() {
            // given
            final String ANONYMOUS = "anonymous";

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, ANONYMOUS, DEFAULT_PAGE_REQUEST);
            Page<AuctionArt> result2 = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, ANONYMOUS, DEFAULT_PAGE_REQUEST);

            assertAll(
                    () -> assertThat(result1.getContent()).isEmpty(),
                    () -> assertThat(result1.getTotalElements()).isEqualTo(0),
                    () -> assertThat(result2.getContent()).isEmpty(),
                    () -> assertThat(result2.getTotalElements()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test2() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, DATE_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, DATE_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test3() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, DATE_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, DATE_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test4() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, PRICE_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(1000, 5000, 9000, 13000, 17000, 21000),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, PRICE_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(3000, 7000, 11000, 15000, 19000, 23000),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test5() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, PRICE_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(21000, 17000, 13000, 9000, 5000, 1000),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, PRICE_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(23000, 19000, 15000, 11000, 7000, 3000),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ????????? ?????? ?????? ????????????")
        void test6() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingLikeMarking(auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, FAVORITE_COUNT_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(1, 3, 5, 7, 9, 11),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, FAVORITE_COUNT_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(2, 4, 6, 8, 10, 12),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ????????? ?????? ?????? ????????????")
        void test7() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingLikeMarking(auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, FAVORITE_COUNT_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(11, 9, 7, 5, 3, 1),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, FAVORITE_COUNT_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(12, 10, 8, 6, 4, 2),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test8() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, BID_COUNT_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 3L, 5L, 7L, 9L, 11L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(1, 3, 5, 7, 9, 11),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, BID_COUNT_ASC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(2L, 4L, 6L, 8L, 10L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(2, 4, 6, 8, 10, 12),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }

        @Test
        @DisplayName("?????? ?????? ?????? ????????????")
        void test9() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createArtList(owner, TOTAL_ELEMENTS, AUCTION, COMMON_HASHTAG);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> resultByHashtagA = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_A, BID_COUNT_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content1 = resultByHashtagA.getContent();
            long totalElements1 = resultByHashtagA.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(6),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(11L, 9L, 7L, 5L, 3L, 1L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(11, 9, 7, 5, 3, 1),
                    () -> assertThat(totalElements1).isEqualTo(6)
            );

            Page<AuctionArt> resultByHashtagB = auctionArtComplexSearchService.getAuctionArtListByHashtag(HASHTAG_B, BID_COUNT_DESC, DEFAULT_PAGE_REQUEST);
            List<AuctionArt> content2 = resultByHashtagB.getContent();
            long totalElements2 = resultByHashtagB.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(6),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 10L, 8L, 6L, 4L, 2L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(12, 10, 8, 6, 4, 2),
                    () -> assertThat(totalElements2).isEqualTo(6)
            );
        }
    }

    private void proceedingLikeMarking(List<Art> result) {
        List<Long> artIdList = result.stream()
                .map(Art::getId)
                .sorted(Long::compare)
                .toList();

        int index = 1;
        for (Long artId : artIdList) {
            for (long memberId = 1; memberId <= index; memberId++) {
                favoriteRepository.save(Favorite.favoriteMarking(artId, memberId));
            }
            index += 1;
        }
    }

    private void proceedingBid(List<Auction> auctions, List<Art> auctionArts) {
        auctionArts.sort(Comparator.comparingLong(Art::getId));
        List<Member> memberList = createMemberList(auctionArts.size());

        for (int i = 0; i < auctionArts.size(); i++) {
            Auction auction = auctions.get(i);
            Art art = auctionArts.get(i);

            int bidAmount = auction.getBidAmount();
            for (int j = 0; j < i + 1; j++) {
                Member bidder = memberList.get(j);
                auction.applyNewBid(bidder, bidAmount);
                auctionRecordRepository.save(AuctionRecord.createAuctionRecord(auction, bidder, bidAmount));
                bidAmount += 1_000;
            }
        }
    }

    private Member createOwner() {
        Member owner = memberRepository.save(MemberFixture.A.toMember());
        pointDetailRepository.save(PointDetail.insertPointDetail(owner, PointType.CHARGE, INIT_AVAILABLE_POINT));
        return owner;
    }

    private List<Member> createMemberList(int totalElements) {
        List<Member> members = new ArrayList<>();
        List<PointDetail> pointDetails = new ArrayList<>();
        for (int i = 2; i <= totalElements + 1; i++) {
            Member member = Member.builder()
                    .name("user" + i)
                    .nickname("user" + i)
                    .loginId("user" + i)
                    .password(Password.encrypt("abcABC123!@#", PasswordEncoderUtils.getEncoder()))
                    .school("???????????????")
                    .address(Address.of(12345, "??????", "??????"))
                    .phone(generateRandomPhoneNumber())
                    .email(Email.from(generateRandomEmail()))
                    .build();
            members.add(member);
            pointDetails.add(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        }
        List<Member> result = memberRepository.saveAllAndFlush(members);
        pointDetailRepository.saveAllAndFlush(pointDetails);
        return result;
    }

    private static String generateRandomPhoneNumber() {
        String result = "010";
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        result += String.valueOf((int) (Math.random() * 9000 + 1000));
        return result;
    }

    private static String generateRandomEmail() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + "@gmail.com";
    }

    public List<Art> createArtList(Member owner, int totalElements, ArtType artType, List<List<String>> hashtagList) {
        List<Art> arts = new ArrayList<>();
        int price = 1_000;
        for (int i = 1; i <= totalElements; i++) {
            arts.add(
                    Art.builder()
                            .owner(owner)
                            .name("name" + i)
                            .description("name" + i)
                            .artType(artType)
                            .price(price)
                            .uploadImage(UploadImage.of("abc.png", generateRandomStorageName()))
                            .hashtags(new HashSet<>(hashtagList.get(i - 1)))
                            .build()
            );
            price += 1_000;
        }

        List<Art> result = artRepository.saveAllAndFlush(arts);
        for (int i = 0; i < result.size(); i++) {
            ReflectionTestUtils.setField(result.get(i), "registrationDate", LocalDateTime.now().minusDays(30 - i)); // ?????? ?????? ???????????? ??????
        }
        return result;
    }

    private String generateRandomStorageName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10) + ".png";
    }

    private List<Auction> initAuctions(List<Art> artList) {
        List<Auction> result = new ArrayList<>();
        for (Art art : artList) {
            result.add(Auction.initAuction(art, Period.of(currentTime3DayAgo, currentTime3DayLater)));
        }
        return auctionRepository.saveAllAndFlush(result);
    }
}

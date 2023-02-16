package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.UploadImage;
import com.sjiwon.anotherart.art.infra.query.dto.BasicAuctionArt;
import com.sjiwon.anotherart.art.service.dto.response.AuctionArt;
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
import static com.sjiwon.anotherart.common.utils.ArtUtils.*;
import static com.sjiwon.anotherart.common.utils.MemberUtils.INIT_AVAILABLE_POINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@RequiredArgsConstructor
class AuctionArtComplexSearchServiceTest extends ServiceIntegrateTest {
    private final AuctionArtComplexSearchService auctionArtComplexSearchService;

    // 총 작품 12건에 대한 Fetching
    private static final int TOTAL_ELEMENTS = 12;
    private static final int DEFAULT_PAGE_SIZE = 8;
    private static final Pageable DEFAULT_PAGE_REQUEST_0 = PageRequest.of(0, DEFAULT_PAGE_SIZE); // 8건
    private static final Pageable DEFAULT_PAGE_REQUEST_1 = PageRequest.of(1, DEFAULT_PAGE_SIZE); // 4건
    private static final String DATE_ASC = "date";
    private static final String DATE_DESC = "rdate";
    private static final String PRICE_ASC = "price";
    private static final String PRICE_DESC = "rprice";
    private static final String FAVORITE_COUNT_ASC = "like";
    private static final String FAVORITE_COUNT_DESC = "rlike";
    private static final String BID_COUNT_ASC = "count";
    private static final String BID_COUNT_DESC = "rcount";

    @Nested
    @DisplayName("현재 경매중인 작품 페이징 쿼리")
    class findCurrentActiceAuctionArtList {
        @Test
        @DisplayName("기본으로 제공되지 않는 정렬 기준은 빈 리스트가 반환된다")
        void test1() {
            // given
            final String ANONYMOUS = "anonymous";

            // when - then
            Page<AuctionArt> result = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(ANONYMOUS, DEFAULT_PAGE_REQUEST_0);


            assertAll(
                    () -> assertThat(result.getContent()).isEmpty(),
                    () -> assertThat(result.getTotalElements()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("등록 날짜 기준 오름차순")
        void test2() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(DATE_ASC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(DATE_ASC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(9L, 10L, 11L, 12L),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("등록 날짜 기준 내림차순")
        void test3() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(DATE_DESC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(DATE_DESC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(4L, 3L, 2L, 1L),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("입찰 가격 기준 오름차순")
        void test4() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(PRICE_ASC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(PRICE_ASC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(9L, 10L, 11L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(9000, 10000, 11000, 12000),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("입찰 가격 기준 내림차순")
        void test5() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(PRICE_DESC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(12000, 11000, 10000, 9000, 8000, 7000, 6000, 5000),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(PRICE_DESC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(4L, 3L, 2L, 1L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getHighestBidPrice)
                                    .toList()
                    ).containsExactly(4000, 3000, 2000, 1000),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("작품 좋아요 횟수 기준 오름차순")
        void test6() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingLikeMarking(auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(FAVORITE_COUNT_ASC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(1, 2, 3, 4, 5, 6, 7, 8),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(FAVORITE_COUNT_ASC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(9L, 10L, 11L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(9, 10, 11, 12),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("작품 좋아요 횟수 기준 내림차순")
        void test7() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingLikeMarking(auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(FAVORITE_COUNT_DESC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(12, 11, 10, 9, 8, 7, 6, 5),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(FAVORITE_COUNT_DESC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(4L, 3L, 2L, 1L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getLikeMarkingMembers)
                                    .map(List::size)
                                    .toList()
                    ).containsExactly(4, 3, 2, 1),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("입찰 횟수 기준 오름차순")
        void test8() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(BID_COUNT_ASC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(1, 2, 3, 4, 5, 6, 7, 8),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(BID_COUNT_ASC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(9L, 10L, 11L, 12L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(9, 10, 11, 12),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
            );
        }

        @Test
        @DisplayName("입찰 횟수 기준 내림차순")
        void test9() {
            // given
            Member owner = createOwner();
            List<Art> auctionArts = createAuctionArtList(owner, TOTAL_ELEMENTS);
            List<Auction> auctions = initAuctions(auctionArts);
            proceedingBid(auctions, auctionArts);

            // when - then
            Page<AuctionArt> result1 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(BID_COUNT_DESC, DEFAULT_PAGE_REQUEST_0);
            List<AuctionArt> content1 = result1.getContent();
            long totalElements1 = result1.getTotalElements();
            assertAll(
                    () -> assertThat(content1.size()).isEqualTo(8),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L),
                    () -> assertThat(
                            content1.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(12, 11, 10, 9, 8, 7, 6, 5),
                    () -> assertThat(totalElements1).isEqualTo(TOTAL_ELEMENTS)
            );

            Page<AuctionArt> result2 = auctionArtComplexSearchService.getCurrentActiveAuctionArtList(BID_COUNT_DESC, DEFAULT_PAGE_REQUEST_1);
            List<AuctionArt> content2 = result2.getContent();
            long totalElements2 = result2.getTotalElements();
            assertAll(
                    () -> assertThat(content2.size()).isEqualTo(4),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getArt)
                                    .map(BasicAuctionArt::getArtId)
                                    .toList()
                    ).containsExactly(4L, 3L, 2L, 1L),
                    () -> assertThat(
                            content2.stream()
                                    .map(AuctionArt::getBidCount)
                                    .toList()
                    ).containsExactly(4, 3, 2, 1),
                    () -> assertThat(totalElements2).isEqualTo(TOTAL_ELEMENTS)
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
                    .school("경기대학교")
                    .address(Address.of(12345, "기본", "상세"))
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

    public List<Art> createAuctionArtList(Member owner, int totalElements) {
        List<Art> arts = new ArrayList<>();
        for (long i = 1; i <= totalElements; i++) {
            arts.add(
                    Art.builder()
                            .owner(owner)
                            .name(generateRandomName())
                            .description(generateRandomDescription())
                            .artType(AUCTION)
                            .price(1_000)
                            .uploadImage(UploadImage.of("abc.png", generateRandomStorageName()))
                            .hashtags(new HashSet<>(HASHTAGS))
                            .build()
            );
        }

        List<Art> result = artRepository.saveAllAndFlush(arts);
        for (int i = 0; i < result.size(); i++) {
            ReflectionTestUtils.setField(result.get(i), "registrationDate", LocalDateTime.now().minusDays(30 - i)); // 등록 날짜 오름차순 설정
        }
        return result;
    }

    private String generateRandomName() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }

    private String generateRandomDescription() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
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
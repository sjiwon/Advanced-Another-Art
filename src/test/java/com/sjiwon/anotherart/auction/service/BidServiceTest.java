package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.domain.hashtag.HashtagRepository;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.auction.domain.Period;
import com.sjiwon.anotherart.auction.domain.record.AuctionRecordRepository;
import com.sjiwon.anotherart.common.PasswordEncoderUtils;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.*;
import com.sjiwon.anotherart.member.domain.point.PointDetail;
import com.sjiwon.anotherart.member.domain.point.PointDetailRepository;
import com.sjiwon.anotherart.member.domain.point.PointType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@DisplayName("Auction [Bid Concurrency Test] -> 경매 작품 입찰 동시성 테스트")
class BidServiceTest {
    private final BidService bidService;
    private final MemberRepository memberRepository;
    private final PointDetailRepository pointDetailRepository;
    private final ArtRepository artRepository;
    private final HashtagRepository hashtagRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionRecordRepository auctionRecordRepository;

    private static final MemberFixture MEMBER = MemberFixture.A;
    private static final int INIT_AVAILABLE_POINT = 100_000_000;
    private static final LocalDateTime currentTime1DayLater = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime currentTime3DayLater = LocalDateTime.now().plusDays(3);
    private static final List<String> HASHTAGS = List.of("A", "B", "C", "D", "E");

    @BeforeEach
    void before() {
        createParticipateMembers(); // 100명의 더미 사용자 생성
    }

    @AfterEach
    void after() {
        auctionRecordRepository.deleteAll();
        auctionRepository.deleteAll();
        hashtagRepository.deleteAll();
        artRepository.deleteAll();
        pointDetailRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("100명의 사용자가 동일한 경매 작품에 동일한 가격으로 동시에 입찰을 진행한다면 경매 히스토리에는 1건만 등록되어야 한다")
    void test() throws Exception {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // given
        Member owner = createArtOwner();
        Art auctionArt = createAuctionArt(owner);
        Auction auction = initAuction(auctionArt);

        // when
        final int bidAmount = auctionArt.getPrice() + 5_000;
        for (int i = 1; i <= 100; i++) {
            long memberId = i;
            executorService.submit(() -> {
                try {
                    bidService.bid(auction.getId(), memberId, bidAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(auctionRecordRepository.countByAuctionId(auction.getId())).isEqualTo(1);
    }

    private void createParticipateMembers() {
        List<Member> members = new ArrayList<>();
        List<PointDetail> pointDetails = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            Member member = Member.builder()
                    .name("Member" + i)
                    .nickname("Member" + i)
                    .loginId("member" + i)
                    .password(Password.encrypt(MEMBER.getPassword(), PasswordEncoderUtils.getEncoder()))
                    .school("경기대학교")
                    .address(Address.of(MEMBER.getPostcode(), MEMBER.getDefaultAddress(), MEMBER.getDetailAddress()))
                    .phone(generateRandomPhoneNumber())
                    .email(Email.from(generateRandomEmail()))
                    .build();
            members.add(member);
            pointDetails.add(PointDetail.insertPointDetail(member, PointType.CHARGE, INIT_AVAILABLE_POINT));
        }

        memberRepository.saveAll(members);
        pointDetailRepository.saveAll(pointDetails);
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

    private Member createArtOwner() {
        return memberRepository.save(MEMBER.toMember());
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }

    private Auction initAuction(Art art) {
        return auctionRepository.save(Auction.initAuction(art, Period.of(currentTime1DayLater, currentTime3DayLater)));
    }
}
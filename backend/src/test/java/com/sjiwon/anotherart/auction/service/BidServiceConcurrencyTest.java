package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.common.ConcurrencyTest;
import com.sjiwon.anotherart.common.utils.PasswordEncoderUtils;
import com.sjiwon.anotherart.member.domain.*;
import com.sjiwon.anotherart.member.domain.point.PointRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.member.domain.point.PointType.CHARGE;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Auction [Service Layer] -> BidService 동시성 테스트")
class BidServiceConcurrencyTest extends ConcurrencyTest {
    @Autowired
    private BidService bidService;

    private static final int THREAD_COUNT = 10;
    private final List<Long> bidderIds = new ArrayList<>();
    private Auction auction;

    @BeforeEach
    void setUp() {
        Member owner = memberRepository.save(MEMBER_A.toMember());
        Art art = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(art));

        createBidders(); // 10명의 입찰자 생성
    }

    @Test
    @DisplayName("10명의 입찰자가 동시에 동일한 가격으로 입찰을 진행한다면 1명만 입찰에 성공해야 한다")
    void concurrencyBid() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        // when
        final int bidAmount = auction.getHighestBidPrice() + 50_000;
        for (Long bidderId : bidderIds) {
            executorService.submit(() -> {
                try {
                    bidService.bid(auction.getId(), bidderId, bidAmount);
                } catch (Exception e) {
                    log.error(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(getAuctionRecordCount()).isEqualTo(1);
    }

    private Long getAuctionRecordCount() {
        return em.createQuery(
                        "SELECT COUNT(ac)" +
                                " FROM AuctionRecord ac" +
                                " WHERE ac.auction.id = :auctionId",
                        Long.class
                )
                .setParameter("auctionId", auction.getId())
                .getResultList()
                .get(0);
    }

    private void createBidders() {
        List<Member> members = new ArrayList<>();
        List<PointRecord> pointRecords = new ArrayList<>();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            Member member = createMember(i);
            increaseMemberPoint(member);
            members.add(member);
            pointRecords.add(PointRecord.addPointRecord(member, CHARGE, 100_000_000));
        }

        memberRepository.saveAll(members);
        pointRecordRepository.saveAll(pointRecords);
        members.forEach(member -> bidderIds.add(member.getId()));
    }

    private Member createMember(int index) {
        return Member.createMember(
                "name" + index,
                Nickname.from("nick" + index),
                "loginid" + index,
                Password.encrypt("abcABC123!@#", PasswordEncoderUtils.getEncoder()),
                "경기대학교",
                generateRandomPhoneNumber(),
                Email.from("test" + index + "@gmail.com"),
                Address.of(12345, "기본 주소", "상세 주소")
        );
    }

    private String generateRandomPhoneNumber() {
        String first = "010";
        String second = String.valueOf((int) (Math.random() * 9000 + 1000));
        String third = String.valueOf((int) (Math.random() * 9000 + 1000));

        return first + second + third;
    }

    private void increaseMemberPoint(Member member) {
        ReflectionTestUtils.setField(member.getPoint(), "totalPoint", 100_000_000);
        ReflectionTestUtils.setField(member.getPoint(), "availablePoint", 100_000_000);
    }
}

package com.sjiwon.anotherart.auction.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.auction.domain.Auction;
import com.sjiwon.anotherart.auction.facade.BidFacade;
import com.sjiwon.anotherart.common.ConcurrencyTest;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Auction [Service Layer] -> BidService 동시성 테스트")
class BidServiceConcurrencyTest extends ConcurrencyTest {
    @Autowired
    private BidFacade bidFacade;

    private static final int THREAD_COUNT = 10;
    private Auction auction;

    @BeforeEach
    void setUp() {
        Member owner = memberRepository.save(MEMBER_A.toMember());
        Art art = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(art));

        createMembers(THREAD_COUNT); // 10명의 입찰자 생성
    }

    @Test
    @DisplayName("10명의 입찰자가 동시에 동일한 가격으로 입찰을 진행한다면 1명만 입찰에 성공해야 한다")
    void concurrencyBid() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        // when
        final int bidAmount = auction.getHighestBidPrice() + 50_000;
        for (Long bidderId : memberIds) {
            executorService.submit(() -> {
                try {
                    bidFacade.bid(auction.getId(), bidderId, bidAmount);
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
}

package com.sjiwon.anotherart.auction.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.application.usecase.command.BidCommand;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.model.AuctionRecord;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRecordRepository;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.IntegrateTest;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.AUCTION_OPEN_NOW;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("Concurrency")
@DisplayName("Auction -> 경매 작품 입찰 동시성 테스트")
public class BidUseCaseConcurrencyTest extends IntegrateTest {
    @Autowired
    private BidUseCase sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionRecordRepository auctionRecordRepository;

    @PersistenceContext
    private EntityManager em;

    private static final int MEMBER_INIT_POINT = 100_000_000;
    private static final int TOTAL_THREAD_COUNT = 10;

    private Auction auction;
    private final Member[] bidders = new Member[10];

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;
    private AtomicInteger successCount;
    private AtomicInteger failCount;

    @BeforeEach
    void setUp() {
        final Member owner = createMember(MEMBER_A);
        final Art art = artRepository.save(AUCTION_1.toArt(owner));
        auction = auctionRepository.save(AUCTION_OPEN_NOW.toAuction(art));

        final List<MemberFixture> fixtures = Arrays.stream(MemberFixture.values())
                .filter(fixture -> fixture.getName().contains("더미"))
                .limit(10)
                .toList();
        Arrays.setAll(bidders, index -> createMember(fixtures.get(index)));

        executorService = Executors.newFixedThreadPool(TOTAL_THREAD_COUNT);
        countDownLatch = new CountDownLatch(TOTAL_THREAD_COUNT);
        successCount = new AtomicInteger();
        failCount = new AtomicInteger();
    }

    private Member createMember(final MemberFixture fixture) {
        final Member member = fixture.toMember();
        member.increaseTotalPoint(MEMBER_INIT_POINT);
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("10명의 입찰자가 동시에 같은 가격으로 입찰을 진행하면 1명의 입찰자만 성공하고 해당 입찰자만 사용 가능한 포인트가 감소해야 한다")
    void sameBid() throws InterruptedException {
        // given
        final int bidPrice = auction.getHighestBidPrice() + 50_000;

        // when
        for (int i = 0; i < TOTAL_THREAD_COUNT; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    sut.invoke(new BidCommand(bidders[index].getId(), auction.getId(), bidPrice));
                    successCount.getAndIncrement();
                } catch (final Exception e) {
                    failCount.getAndIncrement();
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        executorService.shutdown();
        countDownLatch.await();

        // then
        final Auction findAuction = getAuction(auction.getId());
        final List<AuctionRecord> auctionRecords = auctionRecordRepository.findAll();
        final List<Member> members = memberRepository.findAll();

        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(1),
                () -> assertThat(failCount.get()).isEqualTo(9),
                () -> assertThat(auctionRecords).hasSize(1),
                () -> assertThat(findAuction.getHighestBid().getBidder()).isNotNull(),
                () -> assertThat(findAuction.getHighestBid().getBidPrice()).isEqualTo(bidPrice),
                () -> assertThat(members)
                        .map(Member::getAvailablePoint)
                        .containsExactlyInAnyOrder(
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT - bidPrice
                        ) // any order
        );
    }

    @Test
    @DisplayName("10명의 입찰자가 서로 다른 가격으로 입찰을 진행하면 가장 높은 가격을 비드한 입찰자가 최종 입찰자이고 해당 입찰자만 사용 가능한 포인트가 감소해야 한다")
    void diffBid() throws InterruptedException {
        // given
        final int bidStandard = auction.getHighestBidPrice();
        final Map<Integer, Integer> bidInfo = new HashMap<>();
        for (int index = 0; index < TOTAL_THREAD_COUNT; index++) {
            bidInfo.put(index, bidStandard + (10_000 * index));
        }

        // when
        for (int i = 0; i < TOTAL_THREAD_COUNT; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    sut.invoke(new BidCommand(bidders[index].getId(), auction.getId(), bidInfo.get(index)));
                    successCount.getAndIncrement();
                } catch (final Exception e) {
                    failCount.getAndIncrement();
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        executorService.shutdown();
        countDownLatch.await();

        // then
        final Auction findAuction = getAuction(auction.getId());
        final List<Member> members = memberRepository.findAll();

        assertAll(
                () -> assertThat(findAuction.getHighestBid().getBidder()).isNotNull(),
                () -> assertThat(findAuction.getHighestBid().getBidPrice()).isEqualTo(bidStandard + 10_000 * 9),
                () -> assertThat(members)
                        .map(Member::getAvailablePoint)
                        .containsExactlyInAnyOrder(
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT - (bidStandard + 10_000 * 9)
                        ) // any order
        );
    }

    private Auction getAuction(final Long auctionId) {
        return em.createQuery("SELECT ac FROM Auction ac WHERE ac.id = :id", Auction.class)
                .setParameter("id", auctionId)
                .getSingleResult();
    }
}

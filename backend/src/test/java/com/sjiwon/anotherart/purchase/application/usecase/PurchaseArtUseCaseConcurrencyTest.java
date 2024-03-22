package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.IntegrateTest;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.repository.PointRecordRepository;
import com.sjiwon.anotherart.purchase.application.usecase.command.PurchaseArtCommand;
import com.sjiwon.anotherart.purchase.domain.model.Purchase;
import com.sjiwon.anotherart.purchase.domain.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Tag("Concurrency")
@DisplayName("Purchase -> 일반 작품 구매 동시성 테스트")
class PurchaseArtUseCaseConcurrencyTest extends IntegrateTest {
    @Autowired
    private PurchaseArtUseCase sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    private static final int INIT_POINT = 100_000_000;
    private static final int TOTAL_THREAD_COUNT = 10;

    private Art art;
    private final Member[] buyers = new Member[10];

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;
    private AtomicInteger successCount;
    private AtomicInteger failCount;

    @BeforeEach
    void setUp() {
        final Member owner = memberRepository.save(MEMBER_A.toDomain(INIT_POINT));
        art = artRepository.save(GENERAL_1.toDomain(owner));

        final List<MemberFixture> fixtures = Arrays.stream(MemberFixture.values())
                .filter(it -> it.getName().contains("더미"))
                .limit(TOTAL_THREAD_COUNT)
                .toList();
        Arrays.setAll(buyers, index -> memberRepository.save(fixtures.get(index).toDomain(INIT_POINT)));

        executorService = Executors.newFixedThreadPool(TOTAL_THREAD_COUNT);
        countDownLatch = new CountDownLatch(TOTAL_THREAD_COUNT);
        successCount = new AtomicInteger();
        failCount = new AtomicInteger();
    }

    @Test
    @DisplayName("10명의 사용자가 동시에 일반 작품 구매를 진행하면 단 1명의 사용자가 최종 구매자가 된다")
    void success() throws InterruptedException {
        // when
        for (int i = 0; i < TOTAL_THREAD_COUNT; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    sut.invoke(new PurchaseArtCommand(buyers[index].getId(), art.getId()));
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
        final List<Purchase> purchases = purchaseRepository.findAll();
        final List<PointRecord> pointRecords = pointRecordRepository.findAll();
        final List<Member> members = memberRepository.findAll();

        assertAll(
                () -> assertThat(successCount.get()).isEqualTo(1),
                () -> assertThat(failCount.get()).isEqualTo(9),
                () -> assertThat(purchases).hasSize(1),
                () -> assertThat(purchases.get(0).getArtId()).isEqualTo(art.getId()),
                () -> assertThat(purchases.get(0).getPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(pointRecords).hasSize(2),
                () -> assertThat(pointRecords)
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(PointRecord.Type.SOLD, PointRecord.Type.PURCHASE),
                () -> assertThat(members)
                        .map(Member::getAvailablePoint)
                        .containsExactlyInAnyOrder(
                                INIT_POINT + art.getPrice(), // owner
                                INIT_POINT - art.getPrice(), // buyer
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT,
                                INIT_POINT
                        )
        );
    }
}

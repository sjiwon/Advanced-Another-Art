package com.sjiwon.anotherart.purchase.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.IntegrateTest;
import com.sjiwon.anotherart.common.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import com.sjiwon.anotherart.point.domain.model.PointRecord;
import com.sjiwon.anotherart.point.domain.model.PointType;
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
public class PurchaseArtUseCaseConcurrencyTest extends IntegrateTest {
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

    private static final int MEMBER_INIT_POINT = 100_000_000;
    private static final int TOTAL_THREAD_COUNT = 10;

    private Art art;
    private final Member[] buyers = new Member[10];

    private ExecutorService executorService;
    private CountDownLatch countDownLatch;
    private AtomicInteger successCount;
    private AtomicInteger failCount;

    @BeforeEach
    void setUp() {
        final Member owner = createMember(MEMBER_A);
        art = artRepository.save(GENERAL_1.toArt(owner));

        final List<MemberFixture> fixtures = Arrays.stream(MemberFixture.values())
                .filter(fixture -> fixture.getName().contains("더미"))
                .limit(10)
                .toList();
        Arrays.setAll(buyers, index -> createMember(fixtures.get(index)));

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
                () -> assertThat(purchases.get(0).getArt().getId()).isEqualTo(art.getId()),
                () -> assertThat(purchases.get(0).getPrice()).isEqualTo(art.getPrice()),
                () -> assertThat(pointRecords).hasSize(2),
                () -> assertThat(pointRecords)
                        .map(PointRecord::getType)
                        .containsExactlyInAnyOrder(PointType.SOLD, PointType.PURCHASE),
                () -> assertThat(members)
                        .map(Member::getAvailablePoint)
                        .containsExactlyInAnyOrder(
                                MEMBER_INIT_POINT + art.getPrice(), // owner
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT,
                                MEMBER_INIT_POINT - art.getPrice() // buyer
                        ) // any order
        );
    }
}

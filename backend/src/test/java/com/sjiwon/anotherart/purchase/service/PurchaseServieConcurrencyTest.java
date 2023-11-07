package com.sjiwon.anotherart.purchase.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.common.ConcurrencyTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Purchase [Service Layer] -> PurchaseServieConcurrency 동시성 테스트")
class PurchaseServieConcurrencyTest extends ConcurrencyTest {
    @Autowired
    private PurchaseService purchaseService;

    private static final int THREAD_COUNT = 10;
    private Art art;

    @BeforeEach
    void setUp() {
        final Member owner = memberRepository.save(MEMBER_A.toMember());
        art = artRepository.save(GENERAL_1.toArt(owner));

        createMembers(THREAD_COUNT); // 10명의 구매자 생성
    }

    @Test
    @DisplayName("10명의 구매자가 동시에 일반 작품 구매를 진행한다면 1명만 구매에 성공해야 한다")
    void concurrencyPurchase() throws InterruptedException {
        // given
        final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        final CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);

        // when
        for (final Long buyerId : memberIds) {
            executorService.submit(() -> {
                try {
                    purchaseService.purchaseArt(art.getId(), buyerId);
                } catch (final Exception e) {
                    log.error(e.getMessage());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(getPurchaseRecordCount()).isEqualTo(1);
    }

    private Long getPurchaseRecordCount() {
        return em.createQuery(
                        "SELECT COUNT(p)" +
                                " FROM Purchase p",
                        Long.class
                )
                .getResultList()
                .get(0);
    }
}

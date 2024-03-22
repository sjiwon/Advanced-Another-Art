package com.sjiwon.anotherart.like.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.IntegrateTest;
import com.sjiwon.anotherart.like.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("Concurrency")
@DisplayName("Like -> 작품 좋아요 동시성 테스트")
class MarkArtLikeConcurrencyTest extends IntegrateTest {
    @Autowired
    private MarkArtLikeUseCase sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @PersistenceContext
    private EntityManager em;

    private Member member;
    private Art art;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toDomain());
        art = artRepository.save(GENERAL_1.toDomain(member));
    }

    @Test
    @DisplayName("특정 사용자가 작품 좋아요 등록을 동시에 10번 요청하면 1번만 등록이 되어야 한다")
    void execute() throws InterruptedException {
        // given
        final int threadCount = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    sut.invoke(new MarkArtLikeCommand(member.getId(), art.getId()));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();

        // then
        assertThat(getMarkedCount()).isEqualTo(1);
    }

    private Long getMarkedCount() {
        return em.createQuery(
                        "SELECT COUNT(f)" +
                                " FROM Like f" +
                                " WHERE f.memberId = :memberId AND f.artId = :artId",
                        Long.class
                ).setParameter("memberId", member.getId())
                .setParameter("artId", art.getId())
                .getResultList()
                .get(0);
    }
}

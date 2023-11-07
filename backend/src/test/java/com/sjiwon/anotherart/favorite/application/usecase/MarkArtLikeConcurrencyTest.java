package com.sjiwon.anotherart.favorite.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.config.DatabaseCleanerEachCallbackExtension;
import com.sjiwon.anotherart.common.config.MySqlTestContainersExtension;
import com.sjiwon.anotherart.common.config.RedisTestContainersExtension;
import com.sjiwon.anotherart.favorite.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        MySqlTestContainersExtension.class,
        RedisTestContainersExtension.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Favorite -> 작품 좋아요 동시성 테스트")
public class MarkArtLikeConcurrencyTest {
    @Autowired
    private ManageFavoriteUseCase sut;

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
        member = memberRepository.save(MEMBER_A.toMember());
        art = artRepository.save(GENERAL_1.toArt(member));
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
                    sut.markLike(new MarkArtLikeCommand(member.getId(), art.getId()));
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
                                " FROM Favorite f" +
                                " WHERE f.member.id = :memberId AND f.art.id = :artId",
                        Long.class
                ).setParameter("memberId", member.getId())
                .setParameter("artId", art.getId())
                .getResultList()
                .get(0);
    }
}

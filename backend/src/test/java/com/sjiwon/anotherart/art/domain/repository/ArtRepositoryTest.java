package com.sjiwon.anotherart.art.domain.repository;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_2;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art -> ArtRepository 테스트")
class ArtRepositoryTest extends RepositoryTest {
    @Autowired
    private ArtRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private Member memberA;
    private Member memberB;

    @BeforeEach
    void setUp() {
        memberA = memberRepository.save(MEMBER_A.toDomain());
        memberB = memberRepository.save(MEMBER_B.toDomain());
    }

    @Test
    @DisplayName("이름에 해당하는 작품이 존재하는지 확인한다")
    void existsByNameValue() {
        // given
        sut.save(GENERAL_1.toDomain(memberA));

        // when
        final boolean actual1 = sut.existsByNameValue(GENERAL_1.getName().getValue());
        final boolean actual2 = sut.existsByNameValue(GENERAL_2.getName().getValue());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }

    @Test
    @DisplayName("작품명에 해당하는 작품의 ID를 조회한다")
    void findIdByName() {
        // given
        final Art artA = sut.save(AUCTION_1.toDomain(memberA));
        final Art artB = sut.save(AUCTION_2.toDomain(memberA));

        // when
        final long id1 = sut.findIdByName(artA.getName().getValue());
        final long id2 = sut.findIdByName(artB.getName().getValue());

        // then
        assertAll(
                () -> assertThat(id1).isEqualTo(artA.getId()),
                () -> assertThat(id2).isEqualTo(artB.getId())
        );
    }

    @Test
    @DisplayName("작품 ID + 소유자 ID로 작품을 조회한다")
    void findByIdAndOwnerId() {
        // given
        final Art artA = sut.save(AUCTION_1.toDomain(memberA));
        final Art artB = sut.save(AUCTION_2.toDomain(memberB));

        // when
        final Optional<Art> actual1 = sut.findByIdAndOwnerId(artA.getId(), artA.getOwnerId());
        final Optional<Art> actual2 = sut.findByIdAndOwnerId(artA.getId(), artB.getOwnerId());
        final Optional<Art> actual3 = sut.findByIdAndOwnerId(artB.getId(), artB.getOwnerId());
        final Optional<Art> actual4 = sut.findByIdAndOwnerId(artB.getId(), artA.getOwnerId());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty(),
                () -> assertThat(actual3).isPresent(),
                () -> assertThat(actual4).isEmpty()
        );
    }
}

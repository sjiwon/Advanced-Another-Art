package com.sjiwon.anotherart.like.domain;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.like.domain.model.Like;
import com.sjiwon.anotherart.like.domain.repository.LikeRepository;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Like -> LikeRepository 테스트")
class LikeRepositoryTest extends RepositoryTest {
    @Autowired
    private LikeRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    private Member owner;
    private Member member;
    private Art artA;
    private Art artB;

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toDomain());
        member = memberRepository.save(MEMBER_B.toDomain());

        artA = artRepository.save(AUCTION_1.toDomain(owner));
        artB = artRepository.save(AUCTION_2.toDomain(owner));
    }

    @Test
    @DisplayName("작품 ID에 해당하는 좋아요 기록을 삭제한다")
    void deleteByArtId() {
        sut.save(new Like(artA, owner));
        sut.save(new Like(artA, member));
        sut.save(new Like(artB, owner));
        sut.save(new Like(artB, member));
        assertThat(sut.findAll()).hasSize(4);

        /* delete artA */
        sut.deleteByArtId(artA.getId());
        assertThat(sut.findAll()).hasSize(2);

        /* delete artB */
        sut.deleteByArtId(artB.getId());
        assertThat(sut.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("특정 작품에 대해서 사용자가 좋아요를 기록한 기록을 조회한다")
    void findByArtIdAndMemberId() {
        // given
        sut.save(new Like(artA, owner));
        sut.save(new Like(artB, member));

        // when
        final Optional<Like> favorite1 = sut.findByArtIdAndMemberId(artA.getId(), owner.getId());
        final Optional<Like> favorite2 = sut.findByArtIdAndMemberId(artA.getId(), member.getId());
        final Optional<Like> favorite3 = sut.findByArtIdAndMemberId(artB.getId(), owner.getId());
        final Optional<Like> favorite4 = sut.findByArtIdAndMemberId(artB.getId(), member.getId());

        // then
        assertAll(
                () -> assertThat(favorite1).isPresent(),
                () -> assertThat(favorite2).isEmpty(),
                () -> assertThat(favorite3).isEmpty(),
                () -> assertThat(favorite4).isPresent()
        );
    }
}

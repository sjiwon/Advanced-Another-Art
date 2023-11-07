package com.sjiwon.anotherart.favorite.domain;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.favorite.domain.model.Favorite;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
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

@DisplayName("Favorite -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteRepository sut;

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
        owner = memberRepository.save(MEMBER_A.toMember());
        member = memberRepository.save(MEMBER_B.toMember());

        artA = artRepository.save(AUCTION_1.toArt(owner));
        artB = artRepository.save(AUCTION_2.toArt(owner));
    }

    @Test
    @DisplayName("작품 ID에 해당하는 좋아요 기록을 삭제한다")
    void deleteByArtId() {
        sut.save(Favorite.favoriteMarking(artA, owner));
        sut.save(Favorite.favoriteMarking(artA, member));
        sut.save(Favorite.favoriteMarking(artB, owner));
        sut.save(Favorite.favoriteMarking(artB, member));
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
        sut.save(Favorite.favoriteMarking(artA, owner));
        sut.save(Favorite.favoriteMarking(artB, member));

        // when
        final Optional<Favorite> actual1 = sut.findByArtIdAndMemberId(artA.getId(), owner.getId());
        final Optional<Favorite> actual2 = sut.findByArtIdAndMemberId(artA.getId(), member.getId());
        final Optional<Favorite> actual3 = sut.findByArtIdAndMemberId(artB.getId(), owner.getId());
        final Optional<Favorite> actual4 = sut.findByArtIdAndMemberId(artB.getId(), member.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isPresent(),
                () -> assertThat(actual2).isEmpty(),
                () -> assertThat(actual3).isEmpty(),
                () -> assertThat(actual4).isPresent()
        );
    }
}

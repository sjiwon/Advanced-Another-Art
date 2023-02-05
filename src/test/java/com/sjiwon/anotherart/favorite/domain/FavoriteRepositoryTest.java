package com.sjiwon.anotherart.favorite.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Favorite [Repository Layer] -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    @Test
    @DisplayName("작품에 대한 좋아요 등록을 했는지 조회한다")
    void test1() {
        // given
        Member owner = createMemberA();
        Art generalArt = createGeneralArt(owner);
        Art auctionArt = createAuctionArt(owner);

        Member member = createMemberB();
        favoriteMarking(generalArt.getId(), member.getId());
        sync();

        // when
        boolean actual1 = favoriteRepository.existsByArtIdAndMemberId(generalArt.getId(), member.getId());
        boolean actual2 = favoriteRepository.existsByArtIdAndMemberId(auctionArt.getId(), member.getId());

        // then
        assertThat(actual1).isTrue();
        assertThat(actual2).isFalse();
    }

    @Test
    @DisplayName("좋아요 등록을 한 작품에 대해서 좋아요 취소를 진행한다")
    void test2() {
        // given
        Member owner = createMemberA();
        Art art = createGeneralArt(owner);

        Member member = createMemberB();
        favoriteMarking(art.getId(), member.getId());
        sync();

        // when
        favoriteRepository.deleteByArtIdAndMemberId(art.getId(), member.getId());

        // then
        assertThat(favoriteRepository.existsByArtIdAndMemberId(art.getId(), member.getId())).isFalse();
    }

    private void favoriteMarking(Long artId, Long memberId) {
        favoriteRepository.save(Favorite.favoriteMarking(artId, memberId));
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createGeneralArt(Member owner) {
        return artRepository.save(ArtFixture.B.toArt(owner));
    }

    private Art createAuctionArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner));
    }

    private void sync() {
        em.flush();
        em.clear();
    }
}
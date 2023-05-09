package com.sjiwon.anotherart.favorite.domain;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.common.RepositoryTest;
import com.sjiwon.anotherart.member.domain.Member;
import com.sjiwon.anotherart.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite [Repository Layer] -> FavoriteRepository 테스트")
class FavoriteRepositoryTest extends RepositoryTest {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ArtRepository artRepository;

    private Member member;
    private Art art1;
    private Art art2;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());

        Member owner = memberRepository.save(MEMBER_B.toMember());
        art1 = artRepository.save(AUCTION_1.toArt(owner));
        art2 = artRepository.save(AUCTION_2.toArt(owner));
    }

    @Test
    @DisplayName("특정 작품에 대한 사용자 찜 현황을 삭제한다")
    void deleteFavoriteMarking() {
        // given
        favoriteRepository.save(Favorite.favoriteMarking(art1.getId(), member.getId()));

        // when
        favoriteRepository.deleteFavoriteMarking(art1.getId(), member.getId());

        // then
        assertThat(favoriteRepository.existsByArtIdAndMemberId(art1.getId(), member.getId())).isFalse();
    }

    @Test
    @DisplayName("특정 작품에 대해서 사용자가 찜을 했는지 여부를 확인한다")
    void existsByArtIdAndMemberId() {
        // given
        favoriteRepository.save(Favorite.favoriteMarking(art1.getId(), member.getId()));

        // when
        boolean actual1 = favoriteRepository.existsByArtIdAndMemberId(art1.getId(), member.getId());
        boolean actual2 = favoriteRepository.existsByArtIdAndMemberId(art2.getId(), member.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}

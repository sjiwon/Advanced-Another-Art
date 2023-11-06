package com.sjiwon.anotherart.favorite.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Favorite [Service Layer] -> FavoriteService 테스트")
class FavoriteServiceTest extends ServiceTest {
    @Autowired
    private FavoriteService favoriteService;

    private Member member;
    private Art art;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MEMBER_A.toMember());

        final Member owner = memberRepository.save(MEMBER_B.toMember());
        art = artRepository.save(AUCTION_1.toArt(owner));
    }

    @Nested
    @DisplayName("찜 등록")
    class like {
        @Test
        @DisplayName("이미 찜 등록된 스터디를 찜할 수 없다")
        void throwExceptionByAlreadyFavoriteMarked() {
            // given
            favoriteService.like(art.getId(), member.getId());

            // when - then
            assertThatThrownBy(() -> favoriteService.like(art.getId(), member.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(FavoriteErrorCode.ALREADY_FAVORITE_MARKED.getMessage());
        }

        @Test
        @DisplayName("찜 등록에 성공한다")
        void success() {
            // when
            final Long favoriteId = favoriteService.like(art.getId(), member.getId());

            // then
            final Favorite favorite = favoriteRepository.findById(favoriteId).orElseThrow();
            assertAll(
                    () -> assertThat(favorite.getArtId()).isEqualTo(art.getId()),
                    () -> assertThat(favorite.getMemberId()).isEqualTo(member.getId())
            );
        }
    }

    @Nested
    @DisplayName("찜 취소")
    class cancel {
        @Test
        @DisplayName("찜 등록이 되지 않은 스터디를 취소할 수 없다")
        void throwExceptionByNotFavoriteMarked() {
            assertThatThrownBy(() -> favoriteService.cancel(art.getId(), member.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(FavoriteErrorCode.NOT_FAVORITE_MARKED.getMessage());
        }

        @Test
        @DisplayName("찜 취소에 성공한다")
        void success() {
            // given
            final Favorite favorite = favoriteRepository.save(Favorite.favoriteMarking(art.getId(), member.getId()));

            // when
            favoriteService.cancel(art.getId(), member.getId());

            // then
            assertThat(favoriteRepository.existsById(favorite.getId())).isFalse();
        }
    }
}

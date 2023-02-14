package com.sjiwon.anotherart.favorite.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.favorite.domain.Favorite;
import com.sjiwon.anotherart.favorite.exception.FavoriteErrorCode;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Favorite [Service Layer] -> FavoriteService 테스트")
@RequiredArgsConstructor
class FavoriteServiceTest extends ServiceIntegrateTest {
    private final FavoriteService favoriteService;

    @Nested
    @DisplayName("작품 좋아요 등록")
    class like {
        @Test
        @DisplayName("작품 소유자는 본인 작품에 대한 좋아요 등록을 할 수 없다")
        void test1() {
            // given
            Member owner = createMemberA();
            Art art = createArt(owner);

            // when - then
            assertThatThrownBy(() -> favoriteService.addLike(art.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER.getMessage());
        }

        @Test
        @DisplayName("이미 좋아요 등록을 한 작품에 대해서 또 다시 좋아요 등록을 할 수 없다")
        void test2() {
            // given
            Member owner = createMemberA();
            Art art = createArt(owner);

            Member member = createMemberB();
            proceedingLikeMarking(art.getId(), member.getId());

            // when - then
            assertThatThrownBy(() -> favoriteService.addLike(art.getId(), member.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(FavoriteErrorCode.ALREADY_LIKE_MARKING.getMessage());
        }

        @Test
        @DisplayName("작품에 대한 좋아요 등록을 완료한다")
        void test3() {
            // given
            Member owner = createMemberA();
            Art art = createArt(owner);

            Member member = createMemberB();

            // when
            favoriteService.addLike(art.getId(), member.getId());

            // then
            assertThat(favoriteRepository.existsByArtIdAndMemberId(art.getId(), member.getId())).isTrue();
        }
    }

    @Nested
    @DisplayName("작품 좋아요 취소")
    class likeCancel {
        @Test
        @DisplayName("작품 소유자는 본인 작품에 대한 좋아요 취소를 할 수 없다")
        void test1() {
            // given
            Member owner = createMemberA();
            Art art = createArt(owner);

            // when - then
            assertThatThrownBy(() -> favoriteService.removeLike(art.getId(), owner.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(FavoriteErrorCode.INVALID_LIKE_REQUEST_BY_ART_OWNER.getMessage());
        }

        @Test
        @DisplayName("이미 취소하였거나 좋아요 등록을 한 적이 없는 작품에 대해서 좋아요 취소를 진행할 수 없다")
        void test2() {
            // given
            Member owner = createMemberA();
            Art art = createArt(owner);

            Member member = createMemberB();

            // when - then
            assertThatThrownBy(() -> favoriteService.removeLike(art.getId(), member.getId()))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(FavoriteErrorCode.NEVER_OR_ALREADY_CANCEL.getMessage());
        }

        @Test
        @DisplayName("작품에 대한 좋아요 등록을 완료한다")
        void test3() {
            // given
            Member owner = createMemberA();
            Art art = createArt(owner);

            Member member = createMemberB();
            proceedingLikeMarking(art.getId(), member.getId());

            // when
            favoriteService.removeLike(art.getId(), member.getId());

            // then
            assertThat(favoriteRepository.existsByArtIdAndMemberId(art.getId(), member.getId())).isFalse();
        }
    }

    private void proceedingLikeMarking(Long artId, Long memberId) {
        favoriteRepository.save(Favorite.favoriteMarking(artId, memberId));
    }

    private Member createMemberA() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Member createMemberB() {
        return memberRepository.save(MemberFixture.B.toMember());
    }

    private Art createArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }
}
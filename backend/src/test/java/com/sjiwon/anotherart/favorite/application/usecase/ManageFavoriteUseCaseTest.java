package com.sjiwon.anotherart.favorite.application.usecase;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.favorite.application.usecase.command.CancelArtLikeCommand;
import com.sjiwon.anotherart.favorite.application.usecase.command.MarkArtLikeCommand;
import com.sjiwon.anotherart.favorite.domain.model.Favorite;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.favorite.exception.FavoriteException;
import com.sjiwon.anotherart.favorite.exception.FavoriteExceptionCode;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Favorite -> ManageFavoriteUseCase 테스트")
class ManageFavoriteUseCaseTest extends UnitTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final ManageFavoriteUseCase sut = new ManageFavoriteUseCase(
            artRepository,
            memberRepository,
            favoriteRepository
    );

    private final Member host = MEMBER_A.toMember().apply(1L);
    private final Art art = GENERAL_1.toArt(host).apply(1L);

    @Nested
    @DisplayName("작품 좋아요 등록")
    class MarkLike {
        private final MarkArtLikeCommand command = new MarkArtLikeCommand(host.getId(), art.getId());

        @Test
        @DisplayName("해당 작품을 좋아요 등록한다")
        void success() {
            // given
            given(artRepository.getById(command.artId())).willReturn(art);
            given(memberRepository.getById(command.memberId())).willReturn(host);

            final Favorite favorite = Favorite.favoriteMarking(art, host);
            given(favoriteRepository.save(any(Favorite.class))).willReturn(favorite);

            // when
            final Long savedFavoriteId = sut.markLike(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).getById(command.artId()),
                    () -> verify(memberRepository, times(1)).getById(command.memberId()),
                    () -> verify(favoriteRepository, times(1)).save(any(Favorite.class)),
                    () -> assertThat(savedFavoriteId).isEqualTo(favorite.getId())
            );
        }
    }

    @Nested
    @DisplayName("작품 좋아요 취소")
    class CancelLike {
        private final CancelArtLikeCommand command = new CancelArtLikeCommand(host.getId(), art.getId());

        @Test
        @DisplayName("좋아요 등록한 기록이 없다면 취소할 수 없다")
        void throwExceptionByFavoriteRecordNotFound() {
            // given
            given(favoriteRepository.findByArtIdAndMemberId(command.artId(), command.memberId())).willReturn(Optional.empty());

            // when - then
            assertThatThrownBy(() -> sut.cancelLike(command))
                    .isInstanceOf(FavoriteException.class)
                    .hasMessage(FavoriteExceptionCode.FAVORITE_MARKING_NOT_FOUND.getMessage());

            assertAll(
                    () -> verify(favoriteRepository, times(1)).findByArtIdAndMemberId(command.artId(), command.memberId()),
                    () -> verify(favoriteRepository, times(0)).delete(any(Favorite.class))
            );
        }

        @Test
        @DisplayName("좋아요 등록을 취소한다")
        void success() {
            // given
            final Favorite favorite = Favorite.favoriteMarking(art, host);
            given(favoriteRepository.findByArtIdAndMemberId(command.artId(), command.memberId())).willReturn(Optional.of(favorite));

            // when
            sut.cancelLike(command);

            // then
            assertAll(
                    () -> verify(favoriteRepository, times(1)).findByArtIdAndMemberId(command.artId(), command.memberId()),
                    () -> verify(favoriteRepository, times(1)).delete(favorite)
            );
        }
    }
}

package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.DeleteArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.repository.HashtagRepository;
import com.sjiwon.anotherart.art.domain.service.ArtDeleter;
import com.sjiwon.anotherart.art.domain.service.ArtDeletionPreInspector;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.AuctionRepository;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.favorite.domain.repository.FavoriteRepository;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> DeleteArtUseCase 테스트")
public class DeleteArtUseCaseTest extends UseCaseTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final FavoriteRepository favoriteRepository = mock(FavoriteRepository.class);
    private final HashtagRepository hashtagRepository = mock(HashtagRepository.class);

    private final ArtDeletionPreInspector artDeletionPreInspector = new ArtDeletionPreInspector(auctionRepository);
    private final ArtDeleter artDeleter = new ArtDeleter(
            auctionRepository,
            favoriteRepository,
            hashtagRepository,
            artRepository
    );
    private final DeleteArtUseCase sut = new DeleteArtUseCase(artRepository, artDeletionPreInspector, artDeleter);

    private final Member owner = MEMBER_A.toMember().apply(1L);

    @Nested
    @DisplayName("일반 작품 삭제")
    class DeleteGeneralArt {
        private Art art;
        private DeleteArtCommand command;

        @BeforeEach
        void setUp() {
            art = GENERAL_1.toArt(owner).apply(1L);
            command = new DeleteArtCommand(art.getId());

            given(artRepository.getById(command.artId())).willReturn(art);
        }

        @Test
        @DisplayName("판매된 작품은 삭제할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_SOLD_ART.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getById(art.getId()),
                    () -> verify(auctionRepository, times(0)).isBidRecordExists(art.getId()),
                    () -> verify(auctionRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(favoriteRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(artRepository, times(0)).deleteById(art.getId())
            );
        }

        @Test
        @DisplayName("일반 작품을 삭제한다")
        void success() {
            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).getById(art.getId()),
                    () -> verify(auctionRepository, times(0)).isBidRecordExists(art.getId()),
                    () -> verify(auctionRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(favoriteRepository, times(1)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(1)).deleteByArtId(art.getId()),
                    () -> verify(artRepository, times(1)).deleteById(art.getId())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 삭제")
    class DeleteAuctionArt {
        private Art art;
        private DeleteArtCommand command;

        @BeforeEach
        void setUp() {
            art = AUCTION_1.toArt(owner).apply(1L);
            command = new DeleteArtCommand(art.getId());

            given(artRepository.getById(command.artId())).willReturn(art);
        }

        @Test
        @DisplayName("판매된 작품은 삭제할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_SOLD_ART.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getById(art.getId()),
                    () -> verify(auctionRepository, times(0)).isBidRecordExists(art.getId()),
                    () -> verify(auctionRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(favoriteRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(artRepository, times(0)).deleteById(art.getId())
            );
        }

        @Test
        @DisplayName("경매 작품의 경우 입찰자가 존재하면 삭제할 수 없다")
        void throwExceptionByBidderExists() {
            // given
            given(auctionRepository.isBidRecordExists(art.getId())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).getById(art.getId()),
                    () -> verify(auctionRepository, times(1)).isBidRecordExists(art.getId()),
                    () -> verify(auctionRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(favoriteRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(artRepository, times(0)).deleteById(art.getId())
            );
        }

        @Test
        @DisplayName("일반 작품을 삭제한다")
        void success() {
            // given
            given(auctionRepository.isBidRecordExists(art.getId())).willReturn(false);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).getById(art.getId()),
                    () -> verify(auctionRepository, times(1)).isBidRecordExists(art.getId()),
                    () -> verify(auctionRepository, times(1)).deleteByArtId(art.getId()),
                    () -> verify(favoriteRepository, times(1)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(1)).deleteByArtId(art.getId()),
                    () -> verify(artRepository, times(1)).deleteById(art.getId())
            );
        }
    }
}

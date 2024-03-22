package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.DeleteArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtDeleter;
import com.sjiwon.anotherart.art.domain.service.ArtDeletionPreInspector;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.art.domain.service.ArtWriter;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.art.exception.ArtExceptionCode;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import com.sjiwon.anotherart.auction.domain.service.AuctionWriter;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.like.domain.service.LikeWriter;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.AuctionFixture.경매_현재_진행;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> DeleteArtUseCase 테스트")
class DeleteArtUseCaseTest extends UnitTest {
    private final DeleteArtUseCase sut = new DeleteArtUseCase(
            new ArtReader(artRepository),
            new ArtDeletionPreInspector(
                    new AuctionReader(auctionRepository)
            ),
            new ArtDeleter(
                    new AuctionWriter(auctionRepository, auctionRecordRepository),
                    new LikeWriter(likeRepository),
                    new ArtWriter(artRepository, hashtagRepository)
            )
    );

    private final Member owner = MEMBER_A.toDomain().apply(1L);
    private final Member member = MEMBER_B.toDomain().apply(2L);

    @Nested
    @DisplayName("일반 작품 삭제")
    class DeleteGeneralArt {
        private Art art;
        private DeleteArtCommand command;

        @BeforeEach
        void setUp() {
            art = GENERAL_1.toDomain(owner).apply(1L);
            command = new DeleteArtCommand(art.getId(), owner.getId());
            given(artRepository.findByIdAndOwnerId(command.artId(), command.memberId())).willReturn(Optional.of(art));
        }

        @Test
        @DisplayName("판매된 작품은 삭제할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.CANNOT_DELETE_SOLD_ART.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).findByIdAndOwnerId(command.artId(), command.memberId()),
                    () -> verify(auctionRepository, times(0)).findHighestBidderIdByArtId(art.getId()),
                    () -> verify(auctionRecordRepository, times(0)).deleteAuctionRecords(art.getId()),
                    () -> verify(auctionRepository, times(0)).deleteAuction(any()),
                    () -> verify(likeRepository, times(0)).deleteByArtId(any()),
                    () -> verify(hashtagRepository, times(0)).deleteArtHashtags(art.getId()),
                    () -> verify(artRepository, times(0)).deleteArt(art.getId())
            );
        }

        @Test
        @DisplayName("일반 작품을 삭제한다")
        void success() {
            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).findByIdAndOwnerId(command.artId(), command.memberId()),
                    () -> verify(auctionRepository, times(0)).findHighestBidderIdByArtId(art.getId()),
                    () -> verify(auctionRecordRepository, times(0)).deleteAuctionRecords(art.getId()),
                    () -> verify(auctionRepository, times(0)).deleteAuction(any()),
                    () -> verify(likeRepository, times(1)).deleteByArtId(any()),
                    () -> verify(hashtagRepository, times(1)).deleteArtHashtags(art.getId()),
                    () -> verify(artRepository, times(1)).deleteArt(art.getId())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 삭제")
    class DeleteAuctionArt {
        private Art art;
        private Auction auction;
        private DeleteArtCommand command;

        @BeforeEach
        void setUp() {
            art = AUCTION_1.toDomain(owner).apply(1L);
            auction = 경매_현재_진행.toDomain(art).apply(1L);
            command = new DeleteArtCommand(art.getId(), owner.getId());
            given(artRepository.findByIdAndOwnerId(command.artId(), command.memberId())).willReturn(Optional.of(art));
        }

        @Test
        @DisplayName("판매된 작품은 삭제할 수 없다")
        void throwExceptionByAlreadySold() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.CANNOT_DELETE_SOLD_ART.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).findByIdAndOwnerId(command.artId(), command.memberId()),
                    () -> verify(auctionRepository, times(0)).findHighestBidderIdByArtId(art.getId()),
                    () -> verify(auctionRecordRepository, times(0)).deleteAuctionRecords(auction.getId()),
                    () -> verify(auctionRepository, times(0)).deleteAuction(auction.getId()),
                    () -> verify(likeRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(0)).deleteArtHashtags(art.getId()),
                    () -> verify(artRepository, times(0)).deleteArt(art.getId())
            );
        }

        @Test
        @DisplayName("경매 작품의 경우 입찰자가 존재하면 삭제할 수 없다")
        void throwExceptionByBidderExists() {
            // given
            given(auctionRepository.findHighestBidderIdByArtId(art.getId())).willReturn(member.getId());

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(ArtException.class)
                    .hasMessage(ArtExceptionCode.CANNOT_DELETE_IF_BID_EXISTS.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).findByIdAndOwnerId(command.artId(), command.memberId()),
                    () -> verify(auctionRepository, times(1)).findHighestBidderIdByArtId(art.getId()),
                    () -> verify(auctionRecordRepository, times(0)).deleteAuctionRecords(auction.getId()),
                    () -> verify(auctionRepository, times(0)).deleteAuction(auction.getId()),
                    () -> verify(likeRepository, times(0)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(0)).deleteArtHashtags(art.getId()),
                    () -> verify(artRepository, times(0)).deleteArt(art.getId())
            );
        }

        @Test
        @DisplayName("경매 작품을 삭제한다")
        void success() {
            // given
            given(auctionRepository.findHighestBidderIdByArtId(art.getId())).willReturn(null);
            given(auctionRepository.findIdByArtId(art.getId())).willReturn(auction.getId());

            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).findByIdAndOwnerId(command.artId(), command.memberId()),
                    () -> verify(auctionRepository, times(1)).findHighestBidderIdByArtId(art.getId()),
                    () -> verify(auctionRecordRepository, times(1)).deleteAuctionRecords(auction.getId()),
                    () -> verify(auctionRepository, times(1)).deleteAuction(auction.getId()),
                    () -> verify(likeRepository, times(1)).deleteByArtId(art.getId()),
                    () -> verify(hashtagRepository, times(1)).deleteArtHashtags(art.getId()),
                    () -> verify(artRepository, times(1)).deleteArt(art.getId())
            );
        }
    }
}

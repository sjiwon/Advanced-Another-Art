package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.art.exception.ArtExceptionCode;
import com.sjiwon.anotherart.auction.domain.service.AuctionReader;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> ArtDeletionPreInspector 테스트")
class ArtDeletionPreInspectorTest extends UnitTest {
    private final ArtDeletionPreInspector sut = new ArtDeletionPreInspector(
            new AuctionReader(auctionRepository)
    );

    private final Member owner = MEMBER_A.toDomain().apply(1L);
    private final Member member = MEMBER_B.toDomain().apply(2L);

    @Nested
    @DisplayName("일반 작품에 대한 삭제 사전 검사")
    class GeneralArt {
        @Test
        @DisplayName("작품이 판매되었으면 작품 삭제 사전 검사관을 통과하지 못한다")
        void throwExceptionByAlreadySold() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);
            art.closeSale();

            // when - then
            assertAll(
                    () -> assertThatThrownBy(() -> sut.checkArtCanBeDeleted(art))
                            .isInstanceOf(ArtException.class)
                            .hasMessage(ArtExceptionCode.CANNOT_DELETE_SOLD_ART.getMessage()),
                    () -> verify(auctionRepository, times(0)).findHighestBidderIdByArtId(art.getId())
            );
        }

        @Test
        @DisplayName("작품 삭제 사전 검사관을 통과한다")
        void success() {
            // given
            final Art art = GENERAL_1.toDomain(owner).apply(1L);

            // when - then
            assertAll(
                    () -> assertDoesNotThrow(() -> sut.checkArtCanBeDeleted(art)),
                    () -> verify(auctionRepository, times(0)).findHighestBidderIdByArtId(art.getId())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품에 대한 삭제 사전 검사")
    class AuctionArt {
        @Test
        @DisplayName("작품이 판매되었으면 작품 삭제 사전 검사관을 통과하지 못한다")
        void throwExceptionByAlreadySold() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            art.closeSale();

            // when - then
            assertAll(
                    () -> assertThatThrownBy(() -> sut.checkArtCanBeDeleted(art))
                            .isInstanceOf(ArtException.class)
                            .hasMessage(ArtExceptionCode.CANNOT_DELETE_SOLD_ART.getMessage()),
                    () -> verify(auctionRepository, times(0)).findHighestBidderIdByArtId(art.getId())
            );
        }

        @Test
        @DisplayName("경매 작품의 경우 입찰자가 존재하면 작품 삭제 사전 검사관을 통과하지 못한다")
        void throwExceptionByBidderExists() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            given(auctionRepository.findHighestBidderIdByArtId(art.getId())).willReturn(member.getId());

            // when
            assertAll(
                    () -> assertThatThrownBy(() -> sut.checkArtCanBeDeleted(art))
                            .isInstanceOf(ArtException.class)
                            .hasMessage(ArtExceptionCode.CANNOT_DELETE_IF_BID_EXISTS.getMessage()),
                    () -> verify(auctionRepository, times(1)).findHighestBidderIdByArtId(art.getId())
            );
        }

        @Test
        @DisplayName("작품 삭제 사전 검사관을 통과한다")
        void success() {
            // given
            final Art art = AUCTION_1.toDomain(owner).apply(1L);
            given(auctionRepository.findHighestBidderIdByArtId(art.getId())).willReturn(null);

            // when - then
            assertAll(
                    () -> assertDoesNotThrow(() -> sut.checkArtCanBeDeleted(art)),
                    () -> verify(auctionRepository, times(1)).findHighestBidderIdByArtId(art.getId())
            );
        }
    }
}

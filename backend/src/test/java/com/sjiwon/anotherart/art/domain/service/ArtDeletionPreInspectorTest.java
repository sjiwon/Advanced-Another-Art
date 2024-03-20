package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.UnitTest;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> ArtDeletionPreInspector 테스트")
public class ArtDeletionPreInspectorTest extends UnitTest {
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final ArtDeletionPreInspector sut = new ArtDeletionPreInspector(auctionRepository);

    private final Member owner = MEMBER_A.toMember().apply(1L);

    @Nested
    @DisplayName("일반 작품에 대한 삭제 사전 검사")
    class GeneralArt {
        private Art art;

        @BeforeEach
        void setUp() {
            art = GENERAL_1.toArt(owner).apply(1L);
        }

        @Test
        @DisplayName("작품이 판매되었으면 작품 삭제 사전 검사관을 통과하지 못한다")
        void throwExceptionByAlreadySold() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.checkArtCanBeDeleted(art))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_SOLD_ART.getMessage());

            verify(auctionRepository, times(0)).isBidRecordExists(art.getId());
        }

        @Test
        @DisplayName("작품 삭제 사전 검사관을 무사히 통과한다")
        void success() {
            assertDoesNotThrow(() -> sut.checkArtCanBeDeleted(art));

            verify(auctionRepository, times(0)).isBidRecordExists(art.getId());
        }
    }

    @Nested
    @DisplayName("경매 작품에 대한 삭제 사전 검사")
    class AuctionArt {
        private Art art;

        @BeforeEach
        void setUp() {
            art = AUCTION_1.toArt(owner).apply(1L);
        }

        @Test
        @DisplayName("작품이 판매되었으면 작품 삭제 사전 검사관을 통과하지 못한다")
        void throwExceptionByAlreadySold() {
            // given
            art.closeSale();

            // when - then
            assertThatThrownBy(() -> sut.checkArtCanBeDeleted(art))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_SOLD_ART.getMessage());

            verify(auctionRepository, times(0)).isBidRecordExists(art.getId());
        }

        @Test
        @DisplayName("경매 작품의 경우 입찰자가 존재하면 작품 삭제 사전 검사관을 통과하지 못한다")
        void throwExceptionByBidderExists() {
            // given
            given(auctionRepository.isBidRecordExists(art.getId())).willReturn(true);

            // when
            assertThatThrownBy(() -> sut.checkArtCanBeDeleted(art))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.CANNOT_DELETE_IF_BID_EXISTS.getMessage());

            verify(auctionRepository, times(1)).isBidRecordExists(art.getId());
        }

        @Test
        @DisplayName("작품 삭제 사전 검사관을 무사히 통과한다")
        void success() {
            // given
            given(auctionRepository.isBidRecordExists(art.getId())).willReturn(false);

            // when - then
            assertDoesNotThrow(() -> sut.checkArtCanBeDeleted(art));

            verify(auctionRepository, times(1)).isBidRecordExists(art.getId());
        }
    }
}

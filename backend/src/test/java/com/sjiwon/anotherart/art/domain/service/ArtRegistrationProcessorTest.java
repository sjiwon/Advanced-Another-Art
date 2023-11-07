package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.art.domain.model.ArtStatus.ON_SALE;
import static com.sjiwon.anotherart.art.domain.model.ArtType.AUCTION;
import static com.sjiwon.anotherart.art.domain.model.ArtType.GENERAL;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> ArtRegistrationProcessor 테스트")
public class ArtRegistrationProcessorTest extends UseCaseTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);
    private final ArtRegistrationProcessor sut = new ArtRegistrationProcessor(artRepository, auctionRepository);

    private final Member owner = MEMBER_A.toMember().apply(1L);

    @Test
    @DisplayName("일반 작품을 등록한다")
    void generalArt() {
        // given
        final Art art = GENERAL_1.toArt(owner).apply(1L);
        given(artRepository.save(any(Art.class))).willReturn(art);

        // when
        final Art savedArt = sut.execute(art, null, null);

        // then
        assertAll(
                () -> verify(artRepository, times(1)).save(any(Art.class)),
                () -> verify(auctionRepository, times(0)).save(any(Auction.class)),
                () -> assertThat(savedArt.getId()).isEqualTo(art.getId()),
                () -> assertThat(savedArt.getName().getValue()).isEqualTo(art.getName().getValue()),
                () -> assertThat(savedArt.getDescription().getValue()).isEqualTo(art.getDescription().getValue()),
                () -> assertThat(savedArt.getType()).isEqualTo(GENERAL),
                () -> assertThat(savedArt.getUploadImage().getUploadFileName()).isEqualTo(art.getUploadImage().getUploadFileName()),
                () -> assertThat(savedArt.getUploadImage().getLink()).isEqualTo(art.getUploadImage().getLink()),
                () -> assertThat(savedArt.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(savedArt.getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                () -> assertThat(savedArt.getOwner().getId()).isEqualTo(owner.getId())
        );
    }

    @Test
    @DisplayName("경매 작품을 등록한다")
    void auctionArt() {
        // given
        final Art art = AUCTION_1.toArt(owner).apply(1L);
        given(artRepository.save(any(Art.class))).willReturn(art);

        final LocalDateTime now = LocalDateTime.now();

        // when
        final Art savedArt = sut.execute(art, now.plusDays(1), now.plusDays(5));

        // then
        assertAll(
                () -> verify(artRepository, times(1)).save(any(Art.class)),
                () -> verify(auctionRepository, times(1)).save(any(Auction.class)),
                () -> assertThat(savedArt.getId()).isEqualTo(art.getId()),
                () -> assertThat(savedArt.getName().getValue()).isEqualTo(art.getName().getValue()),
                () -> assertThat(savedArt.getDescription().getValue()).isEqualTo(art.getDescription().getValue()),
                () -> assertThat(savedArt.getType()).isEqualTo(AUCTION),
                () -> assertThat(savedArt.getUploadImage().getUploadFileName()).isEqualTo(art.getUploadImage().getUploadFileName()),
                () -> assertThat(savedArt.getUploadImage().getLink()).isEqualTo(art.getUploadImage().getLink()),
                () -> assertThat(savedArt.getStatus()).isEqualTo(ON_SALE),
                () -> assertThat(savedArt.getHashtags()).containsExactlyInAnyOrderElementsOf(art.getHashtags()),
                () -> assertThat(savedArt.getOwner().getId()).isEqualTo(owner.getId())
        );
    }
}

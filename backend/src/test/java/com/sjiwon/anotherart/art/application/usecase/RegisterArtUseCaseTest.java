package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.RegisterArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.domain.service.ArtImageUploader;
import com.sjiwon.anotherart.art.domain.service.ArtRegistrationProcessor;
import com.sjiwon.anotherart.art.domain.service.ArtResourceValidator;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.auction.domain.model.Auction;
import com.sjiwon.anotherart.auction.domain.repository.AuctionRepository;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.file.domain.model.RawFileData;
import com.sjiwon.anotherart.file.utils.converter.FileConverter;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import com.sjiwon.anotherart.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.utils.FileMockingUtils.createSingleMockMultipartFile;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> RegisterArtUseCase 테스트")
public class RegisterArtUseCaseTest extends UnitTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final AuctionRepository auctionRepository = mock(AuctionRepository.class);

    private final ArtResourceValidator artResourceValidator = new ArtResourceValidator(artRepository);
    private final ArtImageUploader artImageUploader = mock(ArtImageUploader.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ArtRegistrationProcessor artRegistrationProcessor = new ArtRegistrationProcessor(artRepository, auctionRepository);
    private final RegisterArtUseCase sut = new RegisterArtUseCase(
            artResourceValidator,
            artImageUploader,
            memberRepository,
            artRegistrationProcessor
    );

    private final Member owner = MEMBER_A.toMember().apply(1L);
    private final RawFileData file = FileConverter.convertImageFile(createSingleMockMultipartFile("1.png", "image/png"));

    @Nested
    @DisplayName("일반 작품 등록")
    class GeneralArt {
        private final RegisterArtCommand command = new RegisterArtCommand(
                owner.getId(),
                GENERAL_1.getName(),
                GENERAL_1.getDescription(),
                GENERAL_1.getType(),
                GENERAL_1.getPrice(),
                null,
                null,
                GENERAL_1.getHashtags(),
                file
        );

        @Test
        @DisplayName("다른 작품이 사용하고 있는 작품명으로 등록할 수 없다")
        void throwExceptionByNameUsedByOther() {
            // given
            given(artRepository.existsByNameValue(command.name().getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).existsByNameValue(command.name().getValue()),
                    () -> verify(artImageUploader, times(0)).uploadImage(command.image()),
                    () -> verify(memberRepository, times(0)).getById(command.ownerId()),
                    () -> verify(artRepository, times(0)).save(any(Art.class)),
                    () -> verify(auctionRepository, times(0)).save(any(Auction.class))
            );
        }

        @Test
        @DisplayName("일반 작품을 등록한다")
        void success() {
            // given
            given(artRepository.existsByNameValue(command.name().getValue())).willReturn(false);
            given(memberRepository.getById(command.ownerId())).willReturn(owner);

            final Art art = GENERAL_1.toArt(owner).apply(1L);
            given(artRepository.save(any(Art.class))).willReturn(art);

            // when
            final Long savedArtId = sut.invoke(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).existsByNameValue(command.name().getValue()),
                    () -> verify(artImageUploader, times(1)).uploadImage(command.image()),
                    () -> verify(memberRepository, times(1)).getById(command.ownerId()),
                    () -> verify(artRepository, times(1)).save(any(Art.class)),
                    () -> verify(auctionRepository, times(0)).save(any(Auction.class)),
                    () -> assertThat(savedArtId).isEqualTo(art.getId())
            );
        }
    }

    @Nested
    @DisplayName("경매 작품 등록")
    class AuctionArt {
        private final RegisterArtCommand command = new RegisterArtCommand(
                owner.getId(),
                AUCTION_1.getName(),
                AUCTION_1.getDescription(),
                AUCTION_1.getType(),
                AUCTION_1.getPrice(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(5),
                AUCTION_1.getHashtags(),
                file
        );

        @Test
        @DisplayName("다른 작품이 사용하고 있는 작품명으로 등록할 수 없다")
        void throwExceptionByNameUsedByOther() {
            // given
            given(artRepository.existsByNameValue(command.name().getValue())).willReturn(true);

            // when - then
            assertThatThrownBy(() -> sut.invoke(command))
                    .isInstanceOf(AnotherArtException.class)
                    .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());

            assertAll(
                    () -> verify(artRepository, times(1)).existsByNameValue(command.name().getValue()),
                    () -> verify(artImageUploader, times(0)).uploadImage(command.image()),
                    () -> verify(memberRepository, times(0)).getById(command.ownerId()),
                    () -> verify(artRepository, times(0)).save(any(Art.class)),
                    () -> verify(auctionRepository, times(0)).save(any(Auction.class))
            );
        }

        @Test
        @DisplayName("경매 작품을 등록한다")
        void success() {
            // given
            given(artRepository.existsByNameValue(command.name().getValue())).willReturn(false);
            given(memberRepository.getById(command.ownerId())).willReturn(owner);

            final Art art = AUCTION_1.toArt(owner).apply(1L);
            given(artRepository.save(any(Art.class))).willReturn(art);

            // when
            final Long savedArtId = sut.invoke(command);

            // then
            assertAll(
                    () -> verify(artRepository, times(1)).existsByNameValue(command.name().getValue()),
                    () -> verify(artImageUploader, times(1)).uploadImage(command.image()),
                    () -> verify(memberRepository, times(1)).getById(command.ownerId()),
                    () -> verify(artRepository, times(1)).save(any(Art.class)),
                    () -> verify(auctionRepository, times(1)).save(any(Auction.class)),
                    () -> assertThat(savedArtId).isEqualTo(art.getId())
            );
        }
    }
}

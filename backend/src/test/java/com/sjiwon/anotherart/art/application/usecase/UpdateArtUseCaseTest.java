package com.sjiwon.anotherart.art.application.usecase;

import com.sjiwon.anotherart.art.application.usecase.command.UpdateArtCommand;
import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.service.ArtReader;
import com.sjiwon.anotherart.art.exception.ArtException;
import com.sjiwon.anotherart.art.exception.ArtExceptionCode;
import com.sjiwon.anotherart.common.UnitTest;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_2;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Art -> UpdateArtUseCase 테스트")
class UpdateArtUseCaseTest extends UnitTest {
    private final UpdateArtUseCase sut = new UpdateArtUseCase(
            new ArtReader(artRepository)
    );

    private final Member owner = MEMBER_A.toDomain().apply(1L);
    private final Art art = AUCTION_1.toDomain(owner).apply(1L);
    private final UpdateArtCommand command = new UpdateArtCommand(
            owner.getId(),
            art.getId(),
            AUCTION_2.getName(),
            AUCTION_2.getDescription(),
            AUCTION_2.getHashtags()
    );

    @Test
    @DisplayName("다른 작품이 변경하려는 작품명을 이미 사용하고 있으면 수정이 불가능하다")
    void throwExceptionByDuplicateName() {
        // given
        given(artRepository.findIdByName(command.name().getValue())).willReturn(art.getId() + 1);

        // when - then
        assertThatThrownBy(() -> sut.invoke(command))
                .isInstanceOf(ArtException.class)
                .hasMessage(ArtExceptionCode.DUPLICATE_NAME.getMessage());

        assertAll(
                () -> verify(artRepository, times(1)).findIdByName(command.name().getValue()),
                () -> verify(artRepository, times(0)).findByIdAndOwnerId(command.artId(), command.memberId())
        );
    }

    @Test
    @DisplayName("작품을 수정한다 (작품명, 설명, 해시태그)")
    void success() {
        // given
        given(artRepository.findIdByName(command.name().getValue())).willReturn(art.getId());
        given(artRepository.findByIdAndOwnerId(command.artId(), command.memberId())).willReturn(Optional.of(art));

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(artRepository, times(1)).findIdByName(command.name().getValue()),
                () -> verify(artRepository, times(1)).findByIdAndOwnerId(command.artId(), command.memberId()),
                () -> assertThat(art.getName().getValue()).isEqualTo(command.name().getValue()),
                () -> assertThat(art.getDescription().getValue()).isEqualTo(command.description().getValue()),
                () -> assertThat(art.getHashtags()).containsExactlyInAnyOrderElementsOf(command.hashtags())
        );
    }
}

package com.sjiwon.anotherart.art.domain.service;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.domain.repository.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.UseCaseTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Art -> ArtResourceValidator 테스트")
public class ArtResourceValidatorTest extends UseCaseTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final ArtResourceValidator sut = new ArtResourceValidator(artRepository);

    private final Member owner = MEMBER_A.toMember().apply(1L);
    private final Art art = GENERAL_1.toArt(owner).apply(1L);

    @Test
    @DisplayName("작품명에 대한 Unique 검증을 진행한다")
    void validatenNameIsUnique() {
        // given
        given(artRepository.existsByNameValue(art.getName().getValue())).willReturn(true);
        given(artRepository.existsByNameValue("diff" + art.getName().getValue())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.validatenNameIsUnique(art.getName().getValue()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());
        assertDoesNotThrow(() -> sut.validatenNameIsUnique("diff" + art.getName().getValue()));
    }

    @Test
    @DisplayName("다른 작품이 해당 이름을 사용하고 있는지 검증한다")
    void validateNameIsInUseByOther() {
        // given
        given(artRepository.isNameUsedByOther(art.getId(), art.getName().getValue())).willReturn(true);
        given(artRepository.isNameUsedByOther(art.getId(), "diff" + art.getName().getValue())).willReturn(false);

        // when - then
        assertThatThrownBy(() -> sut.validateNameIsInUseByOther(art.getId(), art.getName().getValue()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.DUPLICATE_NAME.getMessage());
        assertDoesNotThrow(() -> sut.validateNameIsInUseByOther(art.getId(), "diff" + art.getName().getValue()));
    }
}

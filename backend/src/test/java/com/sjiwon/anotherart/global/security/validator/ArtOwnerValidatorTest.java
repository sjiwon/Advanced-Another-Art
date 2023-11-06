package com.sjiwon.anotherart.global.security.validator;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.GENERAL_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_B;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Global/Security -> ArtOwnerValidator 테스트")
public class ArtOwnerValidatorTest {
    private final ArtRepository artRepository = mock(ArtRepository.class);
    private final ArtOwnerValidator sut = new ArtOwnerValidator(artRepository);

    private final Member owner = MEMBER_A.toMember().apply(1L);
    private final Member anonymous = MEMBER_B.toMember().apply(2L);
    private final Art art = GENERAL_1.toArt(owner).apply(1L);

    @Test
    @DisplayName("작품 주인인지 확인한다")
    void isArtOwner() {
        // given
        given(artRepository.isOwner(art.getId(), owner.getId())).willReturn(true);
        given(artRepository.isOwner(art.getId(), anonymous.getId())).willReturn(false);

        // when
        final boolean actual1 = sut.isArtOwner(art.getId(), owner.getId());
        final boolean actual2 = sut.isArtOwner(art.getId(), anonymous.getId());

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}

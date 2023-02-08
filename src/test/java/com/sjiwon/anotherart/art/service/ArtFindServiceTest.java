package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.domain.ArtRepository;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("Art [Service Layer] -> ArtFindService 테스트")
class ArtFindServiceTest extends ServiceTest {
    @InjectMocks
    private ArtFindService artFindService;

    @Mock
    private ArtRepository artRepository;

    @Test
    @DisplayName("ID(PK)로 작품 조회하기")
    void test1() {
        // given
        final Member owner = MemberFixture.A.toMember();
        final Art art = ArtFixture.A.toArt(owner, HASHTAGS);
        final Long artId = 1L;
        given(artRepository.findById(artId)).willReturn(Optional.ofNullable(art));

        // when
        final Long fakeId = 100L;
        Art actualArt = artFindService.findById(artId);
        assertThatThrownBy(() -> artFindService.findById(fakeId))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.ART_NOT_FOUND.getMessage());

        // then
        assertThat(actualArt).isNotNull();
        assertThat(actualArt.getName()).isEqualTo(art.getName());
        assertThat(actualArt.getDescription()).isEqualTo(art.getDescription());
    }
}
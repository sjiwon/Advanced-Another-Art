package com.sjiwon.anotherart.art.service;

import com.sjiwon.anotherart.art.domain.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.ServiceIntegrateTest;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Service Layer] -> ArtFindService 테스트")
@RequiredArgsConstructor
class ArtFindServiceTest extends ServiceIntegrateTest {
    private final ArtFindService artFindService;

    @Test
    @DisplayName("ID(PK)로 작품 조회하기")
    void test1() {
        // given
        Member owner = createMember();
        Art art = createArt(owner);

        // when
        Art findArt = artFindService.findById(art.getId());
        assertThatThrownBy(() -> artFindService.findById(art.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.ART_NOT_FOUND.getMessage());

        // then
        assertAll(
                () -> assertThat(findArt.getId()).isEqualTo(art.getId()),
                () -> assertThat(findArt.getName()).isEqualTo(art.getName()),
                () -> assertThat(findArt.getHashtagList()).containsAll(HASHTAGS),
                () -> assertThat(findArt.getOwner().getId()).isEqualTo(owner.getId()),
                () -> assertThat(findArt.getOwner().getName()).isEqualTo(owner.getName()),
                () -> assertThat(findArt.getOwner().getNickname()).isEqualTo(owner.getNickname())
        );
    }

    private Member createMember() {
        return memberRepository.save(MemberFixture.A.toMember());
    }

    private Art createArt(Member owner) {
        return artRepository.save(ArtFixture.A.toArt(owner, HASHTAGS));
    }
}
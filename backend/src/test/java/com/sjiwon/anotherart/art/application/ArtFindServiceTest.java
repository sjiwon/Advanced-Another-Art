package com.sjiwon.anotherart.art.application;

import com.sjiwon.anotherart.art.domain.model.Art;
import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.common.ServiceTest;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.sjiwon.anotherart.common.fixture.ArtFixture.AUCTION_1;
import static com.sjiwon.anotherart.common.fixture.MemberFixture.MEMBER_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art [Service Layer] -> ArtFindService 테스트")
class ArtFindServiceTest extends ServiceTest {
    @Autowired
    private ArtFindService artFindService;

    private Member owner;
    private Art art;

    @BeforeEach
    void setUp() {
        owner = memberRepository.save(MEMBER_A.toMember());
        art = artRepository.save(AUCTION_1.toArt(owner));
    }

    @Test
    @DisplayName("ID(PK)로 작품을 조회한다")
    void findById() {
        // when
        assertThatThrownBy(() -> artFindService.findById(art.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.ART_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> artFindService.findByIdWithOwner(art.getId() + 10000L))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.ART_NOT_FOUND.getMessage());

        final Art findArt1 = artFindService.findById(art.getId());
        final Art findArt2 = artFindService.findByIdWithOwner(art.getId());

        // then
        assertAll(
                () -> assertThat(findArt1).isEqualTo(art),
                () -> assertThat(findArt2).isEqualTo(art)
        );
    }
}

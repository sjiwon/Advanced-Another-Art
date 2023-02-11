package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.exception.ArtErrorCode;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.global.exception.AnotherArtException;
import com.sjiwon.anotherart.member.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.sjiwon.anotherart.common.utils.ArtUtils.HASHTAGS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art 도메인 테스트")
class ArtTest {
    private static final ArtFixture ART_A = ArtFixture.A;
    private static final ArtFixture ART_B = ArtFixture.B;
    private static final Member member = MemberFixture.A.toMember();

    @Test
    @DisplayName("작품을 생성한다 + 해시태그를 추가한다")
    void test1(){
        Art art = ART_A.toArt(member, HASHTAGS);

        assertAll(
                () -> assertThat(art.getName()).isEqualTo(ART_A.getName()),
                () -> assertThat(art.getDescription()).isEqualTo(ART_A.getDescription()),
                () -> assertThat(art.getArtType()).isEqualTo(ART_A.getArtType()),
                () -> assertThat(art.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE),
                () -> assertThat(art.getUploadName()).isEqualTo(ART_A.getUploadName()),
                () -> assertThat(art.getHashtagList().size()).isEqualTo(5),
                () -> assertThat(art.getHashtagList()).containsAll(HASHTAGS),
                () -> assertThat(art.getOwner().getName()).isEqualTo(member.getName()),
                () -> assertThat(art.getOwner().getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(art.getOwner().getLoginId()).isEqualTo(member.getLoginId())
        );
    }
    
    @Test
    @DisplayName("작품 설명을 변경한다")
    void test2(){
        // given
        Art art = ART_A.toArt(member, HASHTAGS);
        
        // when
        Assertions.assertThatThrownBy(() -> art.changeDescription(art.getDescription()))
                .isInstanceOf(AnotherArtException.class)
                .hasMessage(ArtErrorCode.NAME_SAME_AS_BEFORE.getMessage());

        final String updateDescription = "Hello World";
        art.changeDescription(updateDescription);
        
        // then
        assertThat(art.getDescription()).isEqualTo(updateDescription);
    }
    
    @Test
    @DisplayName("작품 상태를 변경한다")
    void test3(){
        // given
        Art art = ART_A.toArt(member, HASHTAGS);
        assertThat(art.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE);
        
        // when
        final ArtStatus updateArtStatus = ArtStatus.SOLD_OUT;
        art.changeArtStatus(updateArtStatus);
        
        // then
        assertAll(
                () -> assertThat(art.getArtStatus()).isEqualTo(updateArtStatus),
                () -> assertThat(art.isSoldOut()).isTrue()
        );
    }

    @Test
    @DisplayName("작품의 타입(경매/일반)을 판단한다")
    void test4() {
        // given
        Art auctionArt = ART_A.toArt(member, HASHTAGS);
        Art generalArt = ART_B.toArt(member, HASHTAGS);

        // when
        boolean actual1 = auctionArt.isAuctionType();
        boolean actual2 = generalArt.isAuctionType();

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
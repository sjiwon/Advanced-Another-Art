package com.sjiwon.anotherart.art.domain;

import com.sjiwon.anotherart.art.domain.hashtag.Hashtag;
import com.sjiwon.anotherart.fixture.ArtFixture;
import com.sjiwon.anotherart.fixture.MemberFixture;
import com.sjiwon.anotherart.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Art 도메인 테스트")
class ArtTest {
    private static final ArtFixture ART_A = ArtFixture.A;
    private static final Member member = MemberFixture.A.toMember();

    @Test
    @DisplayName("작품을 생성한다")
    void test1(){
        Art art = ART_A.toArt(member);

        assertAll(
                () -> assertThat(art.getName()).isEqualTo(ART_A.getName()),
                () -> assertThat(art.getDescription()).isEqualTo(ART_A.getDescription()),
                () -> assertThat(art.getArtType()).isEqualTo(ART_A.getArtType()),
                () -> assertThat(art.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE),
                () -> assertThat(art.getUploadImage().getUploadName()).isEqualTo(ART_A.getUploadName()),
                () -> assertThat(art.getHashtags().size()).isEqualTo(0),
                () -> assertThat(art.getOwner().getName()).isEqualTo(member.getName()),
                () -> assertThat(art.getOwner().getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(art.getOwner().getLoginId()).isEqualTo(member.getLoginId())
        );
    }

    @Test
    @DisplayName("작품을 생성한다 + 해시태그를 추가한다")
    void test2(){
        Art art = ART_A.toArt(member);
        art.applyHashtags(new HashSet<>(List.of("A", "B", "C", "D", "E", "E", "E")));

        assertAll(
                () -> assertThat(art.getName()).isEqualTo(ART_A.getName()),
                () -> assertThat(art.getDescription()).isEqualTo(ART_A.getDescription()),
                () -> assertThat(art.getArtType()).isEqualTo(ART_A.getArtType()),
                () -> assertThat(art.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE),
                () -> assertThat(art.getUploadImage().getUploadName()).isEqualTo(ART_A.getUploadName()),
                () -> assertThat(art.getHashtags().size()).isEqualTo(5),
                () -> assertThat(art.getHashtags().stream().map(Hashtag::getName).toList()).contains("A", "B", "C", "D", "E"),
                () -> assertThat(art.getOwner().getName()).isEqualTo(member.getName()),
                () -> assertThat(art.getOwner().getNickname()).isEqualTo(member.getNickname()),
                () -> assertThat(art.getOwner().getLoginId()).isEqualTo(member.getLoginId())
        );
    }
    
    @Test
    @DisplayName("작품 설명을 변경한다")
    void test3(){
        // given
        Art art = ART_A.toArt(member);
        
        // when
        final String updateDescription = "Hello World";
        art.updateDescription(updateDescription);
        
        // then
        assertThat(art.getDescription()).isEqualTo(updateDescription);
    }
    
    @Test
    @DisplayName("작품 상태를 변경한다")
    void test4(){
        // given
        Art art = ART_A.toArt(member);
        assertThat(art.getArtStatus()).isEqualTo(ArtStatus.FOR_SALE);
        
        // when
        final ArtStatus updateArtStatus = ArtStatus.SOLD_OUT;
        art.updateArtStatus(updateArtStatus);
        
        // then
        assertThat(art.getArtStatus()).isEqualTo(updateArtStatus);
        assertThat(art.isSoldOut()).isTrue();
    }
}